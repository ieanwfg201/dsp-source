from datetime import datetime

def getRemainingTimeWindows(timeValue, timePerWindow):
    """ Returns the remaining time windows in the day (including the current window).
    Returns a list of window id's. For e.g., at 06:23 and time window duration of 30, the function would return
    [10,11,12,13,....,47] (id's are 0 based. 1st window's id is 0

    :param timeValue: time value starting from which number of windows remaining has to be found
    :type currentTime : datetime object
    :param timePerWindow: Duration of each time window (should be specified in minutes)
    :type timePerWindow: int
    """
    hours = timeValue.hour
    minutes = timeValue.minute
    totalMinutes = hours * 60 + minutes
    currentWindow = totalMinutes / timePerWindow
    totalTimeWindowsInDay = (24 * 60 + timePerWindow - 1) / timePerWindow
    return range(currentWindow, totalTimeWindowsInDay)

def getRemainingWindowsFromNow(timePerWindow):
    """ Returns the remaining time windows from current time
    Returns a list of window id's. For e.g., at 06:23 and time window duration of 30, the function would return
    [10,11,12,13,....,47] (id's are 0 based. 1st window's id is 0

    :param timePerWindow: Duration of each time window (should be specified in minutes)
    :type timePerWindow: int
    """
    curTime = datetime.now()
    return getRemainingTimeWindows(curTime, timePerWindow)

if __name__ == "__main__":
    windows = getRemainingWindowsFromNow(30)
    print windows
