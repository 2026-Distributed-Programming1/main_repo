package dp.actor;

import dp.consultation.PolicyApplication;
import dp.consultation.ReviewResult;
import dp.consultation.Underwriting;

/**
 * 보험심사자 (InsuranceReviewer)
 */
public class InsuranceReviewer extends Employee {

    public InsuranceReviewer(String name, String contact, String email) {
        super(name, contact, email);
    }

    public Underwriting startUnderwriting(PolicyApplication application) {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        return underwriting;
    }

    public void deliverReviewResult(ReviewResult result) {
        result.deliver();
    }
}
