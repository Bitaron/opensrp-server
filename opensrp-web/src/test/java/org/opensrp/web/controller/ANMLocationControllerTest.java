package org.opensrp.web.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.mockito.Mock;
import org.opensrp.common.util.HttpAgent;
import org.opensrp.domain.DrishtiUser;
import org.opensrp.register.service.ANMDetailsService;

public class ANMLocationControllerTest {

    @Mock
    private ANMDetailsService service;
    @Mock
    private HttpAgent httpAgent;
    @Mock
    private UserController userController;
    @Mock
    private DrishtiUser user;
    private ANMLocationController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new ANMLocationController("http://dristhi_reporting_url/villages", userController, httpAgent);
    }
/*
    @Test
    public void shouldGetANCDetailsForAllANMs() throws Exception {

        when(httpAgent.get("http://dristhi_reporting_url/villages?anm-id=demo1")).
                thenReturn(new HttpResponse(true,
                        new Gson().toJson(new VillagesDTO("district", "PHC X", "phc1", "Sub Center 1", asList("village1", "village2", "village3")))));
       // when(userController.currentUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("demo1");
        when(user.getRoles()).thenReturn(asList("User"));

        ResponseEntity<VillagesDTO> response = controller.villagesForANM();

        assertEquals(new VillagesDTO("district", "PHC X", "phc1", "Sub Center 1", asList("village1", "village2", "village3")), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/
}
