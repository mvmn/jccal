package x.mvmn.util.dates;

public interface Dayable {
	public void setDayValue(int day);

	public void addDayValue(int delta);

	public int getDayValue();

	public Dayable getDay();
}
