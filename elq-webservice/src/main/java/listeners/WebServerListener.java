package listeners;

public interface WebServerListener {
	/**
	 * 
	 * @param success
	 *            true if file uploaded successfully.
	 */
	void onFileUpload(Boolean success);

	/**
	 * 
	 * @param success
	 *            true if Ticker has changed
	 */
	void onTickerChanched(Boolean success);

}
