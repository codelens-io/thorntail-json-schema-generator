package io.codelens.tools.thorntail;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Node {

    public static final String KEY = "KEY";
    
    static final String SCHEMA = "$schema";
    static final String ID = "id";
    static final String TITLE = "title";
    static final String TYPE = "type";
    static final String DOT = ".";
    static final String DESCR = "description";
    static final String PROPERTIES = "properties";
    static final String ADDITIONAL_PROPERTIES = "additionalProperties";
    
    private List<String> keySuggestions = new ArrayList<>();
    private List<Node> children = new ArrayList<>();
    private Node parent;
    private String name;
    private String description;
    private NodeType javaType;

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isKey() {
        return KEY.equals(name);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public NodeType getJavaType() {
        return javaType;
    }

    protected void setJavaType(NodeType type) {
        this.javaType = type;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return parent != null ? parent.getPath() + DOT + name : name;
    }
    
    public void addKeySuggestion(String suggestion) {
        keySuggestions.add(suggestion);
    }

    protected void appendChild(Node node) {
        node.parent = this;
        children.add(node);
    }

    protected boolean isChildKey() {
        return !children.isEmpty() && children.size() == 1 && children.get(0).isKey();
    }
    
    protected List<Node> getKeyChildren() {
        return children.get(0).children;
    }

    protected Optional<Node> getChildByName(String name) {
        return children.stream().filter(node -> node.getName().equals(name)).findFirst();
    }

    protected JsonObjectBuilder toJsonObjectBuilder(boolean writeDescription) {
        JsonObjectBuilder nodeDescriptorObjectBuilder = Json.createObjectBuilder();
        nodeDescriptorObjectBuilder.add(TYPE, javaType.getJsonString());
        if (writeDescription && description != null && !description.trim().isEmpty()) {
            nodeDescriptorObjectBuilder.add(DESCR, description);
        }

        if (!isLeaf()) {
            if (isChildKey()) {
                processChildKeys(nodeDescriptorObjectBuilder, writeDescription);
            } else {
                processSingles(nodeDescriptorObjectBuilder, writeDescription);
            }
        }
        return Json.createObjectBuilder().add(name, nodeDescriptorObjectBuilder);
    }
    
    private void processSingles(JsonObjectBuilder nodeDescriptorObjectBuilder, boolean writeDescription) {
        JsonObjectBuilder properties = Json.createObjectBuilder();
        for (Node child : children) {
            addAll(properties, child.toJsonObjectBuilder(writeDescription).build());
        }
        nodeDescriptorObjectBuilder.add(PROPERTIES, properties);
        nodeDescriptorObjectBuilder.add(ADDITIONAL_PROPERTIES, Boolean.FALSE);
    }
    
    private void processChildKeys(JsonObjectBuilder nodeDescriptorObjectBuilder, boolean writeDescription) {
        JsonObjectBuilder properties = Json.createObjectBuilder();
        for (Node child : getKeyChildren()) {
            addAll(properties, child.toJsonObjectBuilder(writeDescription).build());
        }
        List<String> childKeySuggestions = children.get(0).keySuggestions;
        if (!childKeySuggestions.isEmpty()) {
            JsonObjectBuilder suggestedProperties = Json.createObjectBuilder();
            for (String suggestion : childKeySuggestions) {
                Node suggestionNode = Node.createSuggestionNode(suggestion);
                suggestionNode.children = getKeyChildren();
                addAll(suggestedProperties, suggestionNode.toJsonObjectBuilder(writeDescription).build());
            }
            nodeDescriptorObjectBuilder.add(PROPERTIES, suggestedProperties);
        }
        JsonObjectBuilder additionalProperties = Json.createObjectBuilder().
                add(TYPE, NodeType.OBJECT.getJsonString()).
                add(PROPERTIES, properties).
                add(ADDITIONAL_PROPERTIES, Boolean.FALSE);
        nodeDescriptorObjectBuilder.add(ADDITIONAL_PROPERTIES, additionalProperties);
    }

    private static void addAll(JsonObjectBuilder objectBuilder, Map<String, JsonValue> items) {
        items.forEach(objectBuilder::add);
    }

    protected static Node createNode(String name, String description, NodeType type) {
        Node node = new Node();
        node.name = name;
        node.description = description;
        node.javaType = type;
        return node;
    }
    
    private static Node createSuggestionNode(String name) {
        Node node = new Node();
        node.name = name;
        node.javaType = NodeType.OBJECT;
        return node;
    }
}
