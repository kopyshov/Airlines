package model;

public class Airline {
    private Long id;
    private String code;
    private String name;

    public Airline() {
    }

    public Airline(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
