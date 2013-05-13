package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.scheduling.fpMethodStrategy.FPMethodStrategyFactory;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.BOOLEAN_TRUE_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.HIGH_PRIORITY_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.FP_METHOD_CHANGED_COMMCARE_FIELD_VALUE;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.NO_FP_METHOD_COMMCARE_FIELD_VALUE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION_MILESTONE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

@Service
public class ECSchedulingService {
    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final Schedule fpComplicationSchedule = new Schedule(EC_SCHEDULE_FP_COMPLICATION, asList(EC_SCHEDULE_FP_COMPLICATION_MILESTONE));

    @Autowired
    public ECSchedulingService(ScheduleTrackingService scheduleTrackingService, ActionService actionService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
    }

    public void enrollToFPComplications(String entityId, String currentFPMethod, String isHighPriority, String submissionDate) {
        if (isCoupleHighPriority(isHighPriority) && isFPMethodNone(currentFPMethod)) {
            scheduleTrackingService.enroll(new EnrollmentRequest(entityId, fpComplicationSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                    parse(submissionDate), null, null, null, null, null));
        }
    }

    public void updateFPComplications(FamilyPlanningUpdateRequest request, EligibleCouple couple) {
        if (!FP_METHOD_CHANGED_COMMCARE_FIELD_VALUE.equals(request.fpUpdate())) {
            return;
        }
        if (isFPMethodNone(request.currentMethod())
                && isCoupleHighPriority(couple.details().get(HIGH_PRIORITY_COMMCARE_FIELD_NAME))
                && !isEnrolledToSchedule(request.caseId(), fpComplicationSchedule.name())) {
            scheduleTrackingService.enroll(new EnrollmentRequest(request.caseId(), fpComplicationSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                    parse(request.familyPlanningMethodChangeDate()), null, null, null, null, null));
        } else if (isEnrolledToSchedule(request.caseId(), fpComplicationSchedule.name())) {
            scheduleTrackingService.fulfillCurrentMilestone(request.caseId(), fpComplicationSchedule.name(), parse(request.familyPlanningMethodChangeDate()));
        }
    }

    public void registerEC(FPProductInformation fpInfo) {
        FPMethodStrategyFactory
                .create(scheduleTrackingService, actionService, fpInfo.currentFPMethod())
                .registerEC(fpInfo);
    }

    public void fpChange(FPProductInformation fpInfo) {
        FPMethodStrategyFactory
                .create(scheduleTrackingService, actionService, fpInfo.previousFPMethod())
                .unEnrollFromPreviousScheduleAsFPMethodChanged(fpInfo);

        FPMethodStrategyFactory
                .create(scheduleTrackingService, actionService, fpInfo.currentFPMethod())
                .enrollToNewScheduleForNewFPMethod(fpInfo);
    }

    public void renewFPProduct(FPProductInformation fpInfo) {
        FPMethodStrategyFactory
                .create(scheduleTrackingService, actionService, fpInfo.currentFPMethod())
                .renewFPProduct(fpInfo);
    }

    public void fpFollowup(FPProductInformation fpInfo) {
        FPMethodStrategyFactory
                .create(scheduleTrackingService, actionService, fpInfo.currentFPMethod())
                .fpFollowup(fpInfo);
    }

    private boolean isCoupleHighPriority(String isHighPriorityField) {
        return BOOLEAN_TRUE_COMMCARE_VALUE.equalsIgnoreCase(isHighPriorityField);
    }

    private boolean isFPMethodNone(String currentFPMethod) {
        return isBlank(currentFPMethod) || NO_FP_METHOD_COMMCARE_FIELD_VALUE.equalsIgnoreCase(currentFPMethod);
    }

    private boolean isEnrolledToSchedule(String caseId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(caseId, scheduleName) != null;
    }
}