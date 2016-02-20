package com.anapp.tpb.replacement.Storage.TableTemplates;

/**
 * Created by pearson-brayt15 on 27/01/2016.
 */
public class Test extends Task {

    private int percentage;
    private String grade;

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;

    }

    @Override
    public boolean equals(Object o) {
        try {
            Test t = (Test) o;
            if (t.getId() == getId()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
