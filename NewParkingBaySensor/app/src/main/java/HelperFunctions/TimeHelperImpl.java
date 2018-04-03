package HelperFunctions;

import java.util.Calendar;

import Interfaces.TimeHelper;

public class TimeHelperImpl implements TimeHelper {

    @Override
    public Calendar getTiming() {
        return Calendar.getInstance();
    }
}
