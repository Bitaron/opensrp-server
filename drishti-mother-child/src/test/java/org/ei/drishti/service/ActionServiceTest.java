package org.ei.drishti.service;

import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.ActionData;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ActionServiceTest {
    @Mock
    private AllActions allActions;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;

    private ActionService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ActionService(allActions, allMothers, allChildren);
    }

    @Test
    public void shouldSaveAlertActionForMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa", "bherya").withAnm("ANM ID 1", "ANM phone no"));

        DateTime dueDate = DateTime.now().minusDays(1);
        service.alertForMother("Case X", "ANC 1", "due", dueDate);

        verify(allActions).add(new Action("Case X", "ANM ID 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", dueDate)));
    }

    @Test
    public void shouldSaveAlertActionForChild() throws Exception {
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "TC 1", "Child 1", "bherya").withAnm("DEMO ANM"));

        DateTime dueDate = DateTime.now().minusDays(1);
        service.alertForChild("Case X", "OPV 1", "due", dueDate);

        verify(allActions).add(new Action("Case X", "DEMO ANM", ActionData.createAlert("Child 1", "bherya", "TC 1", "OPV 1", "due", dueDate)));
    }

    @Test
    public void shouldCreateADeleteActionForAVisitOfAChild() throws Exception {
        service.deleteAlertForVisitForChild("Case X", "ANM 1", "OPV 1");

        verify(allActions).add(new Action("Case X", "ANM 1", ActionData.deleteAlert("OPV 1")));
    }

    @Test
    public void shouldCreateADeleteActionForAVisitOfAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa", "bherya").withAnm("ANM ID 1", "ANM phone no"));

        service.deleteAlertForVisitForMother("Case X", "ANC 1");

        verify(allActions).add(new Action("Case X", "ANM ID 1", ActionData.deleteAlert("ANC 1")));
    }

    @Test
    public void shouldCreateADeleteAllActionForAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa", "bherya").withAnm("ANM ID 1", "ANM phone no"));

        service.deleteAllAlertsForMother("Case X");

        verify(allActions).addWithDelete(new Action("Case X", "ANM ID 1", ActionData.deleteAllAlerts()));
    }

    @Test
    public void shouldCreateADeleteAllActionForAChild() throws Exception {
        service.deleteAllAlertsForChild("Case X", "DEMO ANM");

        verify(allActions).addWithDelete(new Action("Case X", "DEMO ANM", ActionData.deleteAllAlerts()));
    }

    @Test
    public void shouldReturnAlertsBasedOnANMIDAndTimeStamp() throws Exception {
        List<Action> alertActions = Arrays.asList(new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now())));
        when(allActions.findByANMIDAndTimeStamp("ANM 1", 1010101)).thenReturn(alertActions);

        List<Action> alerts = service.getNewAlertsForANM("ANM 1", 1010101);

        assertEquals(1, alerts.size());
        assertEquals(alertActions, alerts);
    }

    @Test
    public void shouldAddCreateActionForEligibleCoupleRegistration() throws Exception {
        service.registerEligibleCouple("Case X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X");

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.createEligibleCouple("Wife 1", "Husband 1", "EC Number 1", "Village X", "SubCenter X")));
    }

    @Test
    public void shouldAddDeleteActionForEligibleCoupleClose() throws Exception {
        service.closeEligibleCouple("Case X", "ANM X");

        verify(allActions).addWithDelete(new Action("Case X", "ANM X", ActionData.deleteEligibleCouple()));
    }

    @Test
    public void shouldAddActionForPregnancyRegistration() throws Exception {
        service.registerPregnancy("Case X", "EC Number 1", "Thaayi 1", "Mother 1", "ANM X");

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.createPregnancy("EC Number 1", "Thaayi 1", "Mother 1")));
    }
}
