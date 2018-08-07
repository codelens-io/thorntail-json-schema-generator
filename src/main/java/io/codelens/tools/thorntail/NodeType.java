package io.codelens.tools.thorntail;

public enum NodeType {
    NUMBER("number"),
    BOOLEAN("boolean"),
    STRING("string"),
    OBJECT("object"),

    /**
     * leaf with enabled additional properties 
     */
    LIST("object");

    private String jsonString;

    NodeType(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getJsonString() {
        return jsonString;
    }
}
