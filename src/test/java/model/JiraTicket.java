package model;

public class JiraTicket {

    private String id;
    private String self;
    private String key;
    private JiraFields fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JiraFields getFields() {
        return fields;
    }

    public void setFields(JiraFields fields) {
        this.fields = fields;
    }
}
