package x.mvmn.util.dates;

public interface Yearable {
	public void setYearValue(int year);

	public void addYearValue(int delta);

	public int getYearValue();

	public Yearable getYear();
}
