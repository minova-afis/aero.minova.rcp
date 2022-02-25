package aero.minova.rcp.model;

public class TableMetaData {
	private Integer limited;
	private Integer page;
	private Integer totalResults;
	private Integer totalPages;
	private Integer resultsLeft;

	public Integer getLimited() {
		return limited;
	}

	public void setLimited(Integer limited) {
		this.limited = limited;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getResultsLeft() {
		return resultsLeft;
	}

	public void setResultsLeft(Integer resultsLeft) {
		this.resultsLeft = resultsLeft;
	}
}