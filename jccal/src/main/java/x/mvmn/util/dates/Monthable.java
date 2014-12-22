package x.mvmn.util.dates;

public interface Monthable {
	public void setMonthValue(int month);

	public void addMonthValue(int delta);

	public int getMonthValue();

	public Monthable getMonth();
}
