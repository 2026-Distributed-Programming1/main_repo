package dp.actor;

import dp.consultation.ConsultationRequest;
import dp.consultation.PolicyApplication;
import java.time.LocalDate;

/**
 * 판매채널 (SalesChannel)
 * Designer, Agency 의 부모 클래스 (Generalization)
 */
public class SalesChannel {

    private int channelId;
    private String name;
    private String location;
    private LocalDate startDate;

    public SalesChannel(int channelId, String name, String location) {
        this.channelId = channelId;
        this.name = name;
        this.location = location;
        this.startDate = LocalDate.now();
    }

    public void acceptConsultation(ConsultationRequest request) {
        request.accept();
        System.out.println("  [" + name + "] 상담 요청을 수락했습니다.");
    }

    public void manageInterviewSchedule() {
        System.out.println("  [" + name + "] 면담 일정을 관리합니다.");
    }

    public void manageInterviewRecord() {
        System.out.println("  [" + name + "] 면담 기록을 관리합니다.");
    }

    public PolicyApplication createPolicyApplication() {
        return new PolicyApplication();
    }

    public int getChannelId() { return channelId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public LocalDate getStartDate() { return startDate; }
}
