package untitled.example.com.firebasesurvey.domain.model;

/**
 * Created by Amy on 2019/4/2
 */

public class User {
    private String name = "";
    private String uid = "";
    private String phone = "";
    private int age = 0;

    private User(Builder builder) {
        name = builder.name;
        uid = builder.uid;
        phone = builder.phone;
        age = builder.age;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String name;
        private String uid;
        private String phone;
        private int age;

        private Builder() {
        }

        public Builder setName(String val) {
            name = val;
            return this;
        }

        public Builder setUid(String val) {
            uid = val;
            return this;
        }

        public Builder setPhone(String val) {
            phone = val;
            return this;
        }

        public Builder setAge(int val) {
            age = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{ name :"+name+" uid: "+uid+" phone: "+phone+" age: "+age+" }";
    }
}
