package org.mtvs.backend.analysis.dto;

public class RoutineRequestDto {

    private int id;
    private String name;
    private String kindName;
    private String TimeName;
    private int order;
    private String method;
    private int userId;

    public RoutineRequestDto() {
    }

    public RoutineRequestDto(int id, String name, String kindName, String timeName, int order, String method, int userId) {
        this.id = id;
        this.name = name;
        this.kindName = kindName;
        TimeName = timeName;
        this.order = order;
        this.method = method;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public String getTimeName() {
        return TimeName;
    }

    public void setTimeName(String timeName) {
        TimeName = timeName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
