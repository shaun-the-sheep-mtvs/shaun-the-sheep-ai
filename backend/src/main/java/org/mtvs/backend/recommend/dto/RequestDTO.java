package org.mtvs.backend.recommend.dto;

public class RequestDTO {
    private String userId;
    private List<String> concerns;
    private String skinType;

    public RequestDTO(){}

    public RequestDTO(String userId, List<String> concerns, String skinType){
        this.userId = userId;
        this.concerns = concerns;
        this.skinType = skinType;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getConcerns() {
        return concerns;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setConcerns(List<String> concerns) {
        this.concerns = concerns;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "userId='" + userId + '\'' +
                ", concerns=" + concerns +
                ", skinType='" + skinType + '\'' +
                '}';
    }
    
}
