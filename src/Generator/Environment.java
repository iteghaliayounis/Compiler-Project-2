package Generator;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> variables = new HashMap<>();
    private final Environment parent;

    private Environment(Environment parent) {
        this.parent = parent;
    }


    public static Environment root(Map<String, Object> initial) {
        Environment env = new Environment(null);
        if (initial != null) env.variables.putAll(initial);
        return env;
    }

    public Environment child() {
        return new Environment(this);
    }

    public void define(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {
        if (variables.containsKey(name)) return variables.get(name);
        return parent != null ? parent.get(name) : null;
    }
}