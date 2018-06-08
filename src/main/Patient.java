package main;

import java.util.Arrays;

public class Patient {

    public class ValueAddress{
        private String city;
        private String state;
        private String country;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
    public class Telecom{
        private String system;
        private String value;
        private String use;

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getUse() {
            return use;
        }

        public void setUse(String use) {
            this.use = use;
        }
    }
    public class Address{
        private String[] line;
        private String city;
        private String state;
        private String postalCode;
        private String country;

        public String[] getLine() {
            return line;
        }

        public void setLine(String[] line) {
            this.line = line;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
    public class Language{
        private String system;
        private String code;
        private String display;

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }
    public class Name{
        private String use;
        private String family;
        private String[] given;
        private String[] prefix;

        public String getUse() {
            return use;
        }

        public void setUse(String use) {
            this.use = use;
        }

        public String getFamily() {
            return family.replaceAll("[^A-Za-z]","");
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public String getGiven() {
            return Arrays.toString(given).replaceAll("[^A-Za-z]","");
        }

        public void setGiven(String[] given) {
            this.given = given;
        }

        public String[] getPrefix() {
            return prefix;
        }

        public void setPrefix(String[] prefix) {
            this.prefix = prefix;
        }
    }

    private String resourceType;
    private String id;
    private Telecom[] telecom;
    private Address[] address;
    private Name[] name;
    //private Language language;

    private String birthDate;
    private String gender;


    public Name[] getName() {
        return name;
    }

    public void setName(Name[] name) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Telecom[] getTelecom() {
        return telecom;
    }

    public void setTelecom(Telecom[] telecom) {
        this.telecom = telecom;
    }

    public Address[] getAddress() {
        return address;
    }

    public void setAddress(Address[] address) {
        this.address = address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
