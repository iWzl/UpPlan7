package cc.itsc.analysis.reflect;

import java.io.Serializable;
import java.util.Date;

public class ExampleBean implements Serializable {
    private static final long serialVersionUID = -8121418052209958014L;

    private int id;
    private String name;
    private Date createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
