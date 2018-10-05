package StaticsData;

import lombok.Getter;

@Getter
public enum StatesMachine {

	RECORD(1), PLAYBACK(2), CONVERTFILE(3), PROCESSING_REQUEST(4), CALL_CUSTOMER_SERVICE(5), START(
			6), CHECKING_AUTHENTICATION(7);

	private int index;

	private StatesMachine(int index) {
		this.index = index;
	}

}
