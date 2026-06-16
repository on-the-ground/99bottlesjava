package main.java.com.bottles.stage1;

public enum Stdout implements Output {
    INSTANCE;
    public void out(String str) {
        System.out.println(str);
    }
}

