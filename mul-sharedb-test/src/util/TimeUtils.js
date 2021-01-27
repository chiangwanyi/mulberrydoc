class TimeUtils {
    fullTimeString() {
        let d = new Date();
        let month = d.getMonth() + 1;
        let date = d.getDate();
        let hours = d.getHours();
        let minutes = d.getMinutes();
        let seconds = d.getSeconds();
        let milliseconds = d.getMilliseconds();

        month = month > 9 ? `${month}` : `0${month}`;
        date = date > 9 ? `${date}` : `0${date}`;
        hours = hours > 9 ? `${hours}` : `0${hours}`;
        minutes = minutes > 9 ? `${minutes}` : `0${minutes}`;
        seconds = seconds > 9 ? `${seconds}` : `0${seconds}`;
        if (milliseconds < 10) {
            milliseconds = `00${milliseconds}`;
        } else if (milliseconds < 100) {
            milliseconds = `0${milliseconds}`;
        } else {
            milliseconds = `${milliseconds}`;
        }
        return `${d.getFullYear()}-${month}-${date} ${hours}:${minutes}:${seconds}:${milliseconds}`;
    }
}

export default new TimeUtils();