package dp.actor;

/**
 * 설계사 (Designer)
 * SalesChannel 상속
 */
public class Designer extends SalesChannel {

    private String licenseNumber;

    public Designer(int channelId, String name, String location, String licenseNumber) {
        super(channelId, name, location);
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
}
