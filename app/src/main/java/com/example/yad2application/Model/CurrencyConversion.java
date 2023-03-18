package com.example.yad2application.Model;

public class CurrencyConversion {
    String new_amount;
    String new_currency;
    String old_currency;
    String old_amount;

    public String getNew_amount() {
        return new_amount;
    }

    public void setNew_amount(String new_amount) {
        this.new_amount = new_amount;
    }

    public String getNew_currency() {
        return new_currency;
    }

    public void setNew_currency(String new_currency) {
        this.new_currency = new_currency;
    }

    public String getOld_currency() {
        return old_currency;
    }

    public void setOld_currency(String old_currency) {
        this.old_currency = old_currency;
    }

    public String getOld_amount() {
        return old_amount;
    }

    public void setOld_amount(String old_amount) {
        this.old_amount = old_amount;
    }
}
