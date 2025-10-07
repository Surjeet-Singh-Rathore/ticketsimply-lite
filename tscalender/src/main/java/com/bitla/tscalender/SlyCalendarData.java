package com.bitla.tscalender;

import java.util.Calendar;
import java.util.Date;


class SlyCalendarData {

    private Date showDate = null; //current showing date
    private Date selectedStartDate = null; // first selected date
    private Date selectedEndDate = null; // ended selected date
    private Date minDate = null; // min date
    private Date maxDate = null; // max date
    private int selectedHour = 0;
    private int selectedMinutes = 0;


    private boolean firstMonday = true; // is first monday
    private boolean single = true;

    private Integer backgroundColor = null;
    private Integer headerColor = null;
    private Integer headerTextColor = null;
    private Integer textColor = null;
    private Integer selectedColor = null;
    private Integer selectedTextColor = null;
    private Integer timeTheme = null;

    public Date getShowDate() {
        if (showDate == null) {
            if (selectedStartDate != null) {
                showDate = (Date) selectedStartDate.clone();
            } else {
                showDate = Calendar.getInstance().getTime();
            }
        }
        return showDate;
    }


    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    public void setSelectedStartDate(Date selectedStartDate) {
        this.selectedStartDate = selectedStartDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
    public Date getMinDate() {
        if (minDate == null) {
            if (minDate != null) {
                minDate = (Date) minDate.clone();
            } else {
                minDate = Calendar.getInstance().getTime();
            }
        }
        return minDate;
    }



    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }
    public Date getMaxDate() {
        if (maxDate == null) {
            if (maxDate != null) {
                maxDate = (Date) maxDate.clone();
            } else {
              //  maxDate = Calendar.getInstance().getTime();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR,1);
                maxDate = calendar.getTime();
            }
        }
        return maxDate;
    }

    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    public void setSelectedEndDate(Date selectedEndDate) {
        this.selectedEndDate = selectedEndDate;
    }

    public int getSelectedHour() {
        return selectedHour;
    }

    public void setSelectedHour(int selectedHour) {
        this.selectedHour = selectedHour;
    }

    public int getSelectedMinutes() {
        return selectedMinutes;
    }

    public void setSelectedMinutes(int selectedMinutes) {
        this.selectedMinutes = selectedMinutes;
    }

    public boolean isFirstMonday() {
        return firstMonday;
    }

    public void setFirstMonday(boolean firstMonday) {
        this.firstMonday = firstMonday;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(Integer headerColor) {
        this.headerColor = headerColor;
    }

    public Integer getHeaderTextColor() {
        return headerTextColor;
    }

    public void setHeaderTextColor(Integer headerTextColor) {
        this.headerTextColor = headerTextColor;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public Integer getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Integer selectedColor) {
        this.selectedColor = selectedColor;
    }

    public Integer getSelectedTextColor() {
        return selectedTextColor;
    }

    public void setSelectedTextColor(Integer selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public Integer getTimeTheme() {
        return timeTheme;
    }

    public void setTimeTheme(Integer timeTheme) {
        this.timeTheme = timeTheme;
    }
}
