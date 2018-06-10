package main;

public class MedicationRequest {
    public class Subject{
        private String reference;

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }
    public class Extension{
        ValueCodeableConcept valueCodeableConcept;
        public class ValueCodeableConcept{
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public ValueCodeableConcept getValueCodeableConcept() {
            return valueCodeableConcept;
        }

        public void setValueCodeableConcept(ValueCodeableConcept valueCodeableConcept) {
            this.valueCodeableConcept = valueCodeableConcept;
        }
    }
    public class MedicationCodeableConcept{
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private String id;
    private String authoredOn;
    private MedicationCodeableConcept medicationCodeableConcept;
    private Extension[] extension;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthoredOn() {
        return authoredOn;
    }

    public void setAuthoredOn(String authoredOn) {
        this.authoredOn = authoredOn;
    }

    public MedicationCodeableConcept getMedicationCodeableConcept() {
        return medicationCodeableConcept;
    }

    public void setMedicationCodeableConcept(MedicationCodeableConcept medicationCodeableConcept) {
        this.medicationCodeableConcept = medicationCodeableConcept;
    }

    public Extension[] getExtension() {
        return extension;
    }

    public void setExtension(Extension[] extension) {
        this.extension = extension;
    }
}
