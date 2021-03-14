import java.util.List;

public class Response {
    private String contextId;
    private Boolean success;
    private Integer code;
    private List<Product> results;

    public List<Product> getResults() {
        return results;
    }
}
