package common.lib.message;

interface ISMSListener{
	void onDelivered(int resultCode);
	void onSent(int resultCode);
}

