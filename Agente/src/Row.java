import java.time.LocalDate;

class Row {
    private String id; // 1
    private String type; // 2
    private String status;// 3
    private Integer maxValue; // 4
    private Integer minValue; // 5
    private LocalDate lastSamplingTime; //6 sensor
    private LocalDate controlingTime; //6 actuator

    public Row() {
    }
}
