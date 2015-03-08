package com.example.layoutdesign.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;

import com.example.layoutdesign.ApplicationMaster;
import com.example.layoutdesign.ChatifyApplication;
import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.ChatHistoryDataSource;
import com.example.layoutdesign.Model.ChatHistory;
import com.example.layoutdesign.Model.ListModel;
import com.example.layoutdesign.base.CustomListViewAdapter;
import com.example.layoutdesign.base.EmoticonsPagerAdapter;
import com.example.layoutdesign.base.ListFragmentBase;
import com.example.layoutdesign.base.EmoticonsGridAdapter.KeyClickListener;

import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class ChatFragment extends ListFragmentBase implements KeyClickListener {

	public static final String ARG_POSITION = "position";
	public static final String ARG_RECEIVER_NAME = "receivername";
	private static final int NO_OF_EMOTICONS = 54;
	private static final int CAMERA_PIC_REQUEST = 2500;
	private static final int GALLERY_REQUEST = 2501;

	private View popUpView;
	private PopupWindow popupWindow;
	private Bitmap[] emoticons;
	private EditText content;
	private LinearLayout emoticonsCover;
	int currentPosition = -1;
	String currentReceiverName = "";
	private int keyboardHeight;
	private boolean isKeyBoardVisible;
	private LinearLayout parentLayout;
	public Handler mHandler = new Handler();
	private Uri outputFileUri;
	private ImageView emoticonsButton;

	public ChatFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			this.currentPosition = savedInstanceState.getInt(ARG_POSITION);
			this.currentReceiverName = savedInstanceState
					.getString(ARG_RECEIVER_NAME);
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_POSITION, this.currentPosition);
		outState.putString(ARG_RECEIVER_NAME, this.currentReceiverName);
	}
	
	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {

			// Set chat view based on argument passed in
			this.updateChatView(args.getInt(ARG_POSITION),
					args.getString(ARG_RECEIVER_NAME));

		} else if (this.currentPosition != -1) {

			// Set chat view based on saved instance state defined during
			// onCreateView
			this.updateChatView(this.currentPosition, this.currentReceiverName);
		}

		parentLayout = (LinearLayout) getActivity().findViewById(
				R.id.fragment_chat);
		emoticonsCover = (LinearLayout) getActivity().findViewById(
				R.id.footer_for_emoticons);
		popUpView = getActivity().getLayoutInflater().inflate(
				R.layout.emoticons_popup, null);

		this.listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow.isShowing())
					popupWindow.dismiss();
				return false;
			}
		});

		// Defining default height of keyboard which is equal to 230 dip
		final float popUpheight = getResources().getDimension(
				R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);

		// Showing and Dismissing pop up on clicking emoticons button
		emoticonsButton = (ImageView) getActivity().findViewById(
				R.id.emoticons_button);
		emoticonsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!popupWindow.isShowing()) {

					popupWindow.setHeight((int) (keyboardHeight));

					if (isKeyBoardVisible) {
						emoticonsCover.setVisibility(LinearLayout.GONE);
					} else {
						emoticonsCover.setVisibility(LinearLayout.VISIBLE);
					}

					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0,
							0);
					emoticonsButton.setImageDrawable(getResources()
							.getDrawable(R.drawable.ic_action_keyboard));

				} else {
					popupWindow.dismiss();
					emoticonsButton.setImageDrawable(getResources()
							.getDrawable(R.drawable.ic_smiley));
				}

			}
		});

		this.readEmoticons();
		this.enablePopUpView();
		this.checkKeyboardHeight(parentLayout);
		this.enableFooterView();
		this.receiveMessage();

		Button send = (Button) getActivity().findViewById(R.id.sendButton);

		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				sendChatMessage();
			}
		});

		EditText chatText = (EditText) getActivity().findViewById(
				R.id.chat_content);

		TextWatcher tw = new TextWatcher() {
			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// you can check for enter key here
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// SmackManager.smackNotificationListener(currentReceiverName,false,false,false,false);
			}
		};

		chatText.addTextChangedListener(tw);

		//
		// if(SmackManager.getUserVCard(this.currentReceiverName).getAvatar()!=null)
		// userAvatarPic=PreferenceDataSource.decodeByteToBitmap(SmackManager.getUserVCard(this.currentReceiverName).getAvatar());

	}

	public void updateChatView(int position, String receiverName) {

		this.setListData(null);
		this.listView = (ListView) getActivity().findViewById(R.id.list);
		this.listAdapter = new CustomListViewAdapter(getActivity(), this,
				listValues, getActivity().getResources(),
				R.layout.chatlistitemformat);

		this.listView.setAdapter(this.listAdapter);

		this.currentPosition = position;
		this.currentReceiverName = receiverName;
	}

	public void attachPopupButtons() {

		// check if the camera is available or not.
		Button camera = (Button) popUpView.findViewById(R.id.btnCamera);

		if (getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {

			camera.setVisibility(1);
			camera.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					openCameraIntent();
					disableFooterView();
				}
			});
		} else {
			camera.setVisibility(0);
		}

		Button fromfile = (Button) popUpView.findViewById(R.id.btnFile);
		fromfile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				openGalleryIntent();
				disableFooterView();
			}
		});

		Button location = (Button) popUpView.findViewById(R.id.btnLocation);
		location.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				getCurrentLocationAddres();
				disableFooterView();
			}
		});
	}

	public File getPictureDirectory() {
		File pictureDirectory = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		if (pictureDirectory == null) {

			// it is great to use internal directory structure, instead of
			// external as it may not be possible to know the external
			pictureDirectory = new File(getActivity().getFilesDir(), "chatify");
			if (pictureDirectory.exists()) {
			} else {
				pictureDirectory.mkdirs();
			}
		}

		return pictureDirectory;
	}

	public String getTemporaryImageFileName() {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";

		return imageFileName;
	}

	private String getFilePath(Uri contentURI) {

		Cursor cursor = getActivity().getContentResolver().query(contentURI,
				null, null, null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file
								// path
			return contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}

	private void openCameraIntent() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File pictureDirectory = this.getPictureDirectory();

		File imageAbsPath;

		try {

			imageAbsPath = File.createTempFile(getTemporaryImageFileName(), /* prefix */
					".jpg", /* suffix */
					pictureDirectory /* directory */

			);

			outputFileUri = Uri.fromFile(imageAbsPath);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(intent, CAMERA_PIC_REQUEST);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void openGalleryIntent() {

		Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);

		// comma-separated MIME types
		mediaChooser.setType("video/*, images/*");
		startActivityForResult(mediaChooser, GALLERY_REQUEST);
	}

	private String getCurrentLocationAddres() {

		return "";
	}

	// added by syed
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == CAMERA_PIC_REQUEST) {
				outputFileUri = data.getData();
				this.sendFile(this.getFilePath(outputFileUri));

			} else if (requestCode == GALLERY_REQUEST) {

				outputFileUri = data.getData();
				// File file=new File(getRealPathFromURI(outputFileUri));
				new SmackSendReceiveFiles().execute("");

			}
		}
	}

	private Spanned sendFile(String fileLocation) {

		Spanned sentMessage = null;
		File file = new File(fileLocation);

		if (file != null && file.exists()) {

			ProviderManager.getInstance().addIQProvider("query",
					"http://jabber.org/protocol/bytestreams",
					new BytestreamsProvider());
			ProviderManager.getInstance().addIQProvider("query",
					"http://jabber.org/protocol/disco#items",
					new DiscoverItemsProvider());
			ProviderManager.getInstance().addIQProvider("query",
					"http://jabber.org/protocol/disco#info",
					new DiscoverInfoProvider());

			ServiceDiscoveryManager sdm = ServiceDiscoveryManager
					.getInstanceFor(ApplicationMaster.getSmackManager().xmppConnection);

			// try once again the other way.
			if (sdm == null) {

				sdm = new ServiceDiscoveryManager(
						ApplicationMaster.getSmackManager().xmppConnection);
			}

			sdm.addFeature("http://jabber.org/protocol/disco#info");
			sdm.addFeature("jabber:iq:privacy");

			// Create the file transfer manager
			FileTransferManager manager = new FileTransferManager(
					ApplicationMaster.getSmackManager().xmppConnection);

			// Create the outgoing file transfer
			OutgoingFileTransfer transfer = manager
					.createOutgoingFileTransfer(this.currentReceiverName
							+ "/Spark 2.6.3");
			try {

				transfer.sendFile(file, file.getName());
				boolean fileTransferred = true;

				while (!transfer.isDone()) {
					if (transfer.getStatus().equals(Status.error)
							|| transfer.getStatus().equals(Status.cancelled)
							|| transfer.getStatus().equals(Status.refused)) {

						sentMessage = Html.fromHtml(String
								.format("{0} {1}", file.getName(), transfer
										.getStatus().toString()));
						;

						fileTransferred = false;
						break;
					}

					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (fileTransferred) {
					ImageGetter imageGetter = new ImageGetter() {
						public Drawable getDrawable(String source) {
							Bitmap bitmap;
							Drawable d;
							try {
								bitmap = MediaStore.Images.Media.getBitmap(
										getActivity().getContentResolver(),
										outputFileUri);
								d = new BitmapDrawable(getResources(), bitmap);
								d.setBounds(0, 0, d.getIntrinsicWidth(),
										d.getIntrinsicHeight());
								return d;
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

							return null;
						}
					};

					sentMessage = Html.fromHtml("<img src ='" + outputFileUri
							+ "'/>", imageGetter, null);

				}

			} catch (XMPPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return sentMessage;
	}

	private void receiveFile() {

		FileTransferManager manager = new FileTransferManager(
				ApplicationMaster.getSmackManager().xmppConnection);

		manager.addFileTransferListener(new FileTransferListener() {

			public void fileTransferRequest(final FileTransferRequest request) {
				new Thread() {

					@Override
					public void run() {
						IncomingFileTransfer transfer = request.accept();
						File mf = Environment.getExternalStorageDirectory();
						
						File file = new File(mf.getAbsoluteFile()
								+ "/DCIM/Camera/" + transfer.getFileName());
						try {
							
							transfer.recieveFile(file);
							
							while (!transfer.isDone()) {

								try {
									Thread.sleep(1000L);
								} catch (Exception e) {
									Log.e("", e.getMessage());
								}

								if (transfer.getStatus().equals(Status.error)) {
									Log.e("ERROR!!! ", transfer.getError() + "");
								}
								if (transfer.getException() != null) {
									transfer.getException().printStackTrace();
								}
							}
						} catch (Exception e) {
							Log.e("", e.getMessage());
						}
					};
				}.start();
			}
		});
	}

	public void updatChatHistory(Spanned sendText, Spanned receivedText) {

		ListModel item = new ListModel();
		item.setSpanText(sendText);
		item.setSpanText1(receivedText);

		this.listValues.add(item);
		this.listAdapter.notifyDataSetChanged();

		ChatHistory chatHistory = new ChatHistory();
		chatHistory.setReceiver(this.currentReceiverName);
		chatHistory.setDateTime(new Date());
		chatHistory.setSpanText(sendText);
		chatHistory.setSpanText1(receivedText);

		ChatHistoryDataSource ds = new ChatHistoryDataSource(
				ChatifyApplication.getAppContext());
		ds.insert(chatHistory);
	}

	public void sendChatMessage() {

		EditText textBox = (EditText) getActivity().findViewById(
				R.id.chat_content);
		
		if (textBox.getText().toString().length() > 0) {
			Calendar c = Calendar.getInstance();

			Spanned sp = textBox
					.getText()
					.append(Html
							.fromHtml("<small><font size='5' color='#b5b5b5'><i>   "
									+ c.getTime().getHours()
									+ ":"
									+ c.getTime().getMinutes()
									+ "</i></font></small>"));

			String message = new String(Html.toHtml(sp));
			message = message.replaceAll("(?i)<(?!img|/img).*?>", "");
			ApplicationMaster.getSmackManager().sendMessage(
					this.currentReceiverName, Type.chat, message);

			this.updatChatHistory(sp, null);
			textBox.setText("");
		}
	}

	// added by syed
	public void receiveMessage() {
		if (ApplicationMaster.getSmackManager().xmppConnection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			ApplicationMaster.getSmackManager().xmppConnection
					.addPacketListener(new PacketListener() {
						public void processPacket(Packet packet) {
							Message message = (Message) packet;
							if (message.getBody() != null) {
								String fromName = StringUtils
										.parseBareAddress(message.getFrom());
								Log.i("ChatActivity",
										"Got text [" + message.getBody()
												+ "] from [" + fromName + "]");
								Spanned cs = Html.fromHtml(message.getBody(),
										new ImageGetter(), null);
								ListModel item = new ListModel();
								item.setSpanText(cs);

								listValues.add(item);

								ChatHistory chatHistory = new ChatHistory();
								chatHistory.setReceiver(currentReceiverName);
								chatHistory.setDateTime(new Date());
								chatHistory.setSpanText(cs);

								ChatHistoryDataSource ds = new ChatHistoryDataSource(
										ChatifyApplication.getAppContext());
								ds.insert(chatHistory);

								mHandler.post(new Runnable() {
									public void run() {
										listAdapter.notifyDataSetChanged();
									}
								});
							}
						}
					}, filter);
		} else {

		}
	}

	private class ImageGetter implements Html.ImageGetter {

		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source.replace(".png", ""));
			Drawable d = new BitmapDrawable(getResources(), emoticons[id - 1]);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}
	};

	

	@Override
	public void itemClicked(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListData(Bundle data) {
		if (this.listValues == null) {
			this.listValues = new ArrayList<ListModel>();
		}

		ChatHistoryDataSource chatHistoryDS = new ChatHistoryDataSource(
				getActivity());
		List<ChatHistory> historyList = chatHistoryDS.getReceiverMatches(
				currentReceiverName, null);

		for (ChatHistory ch : historyList) {
			ListModel m = new ListModel();
			m.setText(ch.getText());
			this.listValues.add(m);

		}

	}

	/**
	 * Reading all emoticons in local cache
	 */
	private void readEmoticons() {

		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {
			emoticons[i] = getImage((i + 1) + ".png");
		}

	}

	/**
	 * Enabling all content in footer i.e. post window
	 */
	private void enableFooterView() {

		content = (EditText) getActivity().findViewById(R.id.chat_content);
		content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (popupWindow.isShowing()) {

					popupWindow.dismiss();
					emoticonsButton.setImageDrawable(getResources()
							.getDrawable(R.drawable.ic_smiley));
				}

			}
		});
	}

	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;

	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);

						if (previousHeightDiffrence - heightDifference > 50) {
							popupWindow.dismiss();
							// emoticonsButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_smiley));
						} else {
							// emoticonsButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_remove));
						}
						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {

							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {

							isKeyBoardVisible = false;

						}

					}
				});

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}

	}

	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		ViewPager pager = (ViewPager) popUpView
				.findViewById(R.id.emoticons_pager);
		pager.setOffscreenPageLimit(3);

		ArrayList<String> paths = new ArrayList<String>();

		for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
			paths.add(i + ".png");
		}

		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(
				getActivity(), paths, this);
		pager.setAdapter(adapter);

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);

		Button backSpace = (Button) popUpView.findViewById(R.id.back);
		backSpace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
						0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				content.dispatchKeyEvent(event);
			}
		});

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				emoticonsCover.setVisibility(LinearLayout.GONE);
			}
		});

		// attach button onclick after open popup
		attachPopupButtons();
	}

	/**
	 * For loading smileys from assets
	 */
	private Bitmap getImage(String path) {
		AssetManager mngr = getActivity().getAssets();
		InputStream in = null;
		try {
			in = mngr.open("emoticons/" + path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bitmap temp = BitmapFactory.decodeStream(in, null, null);
		return temp;
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	public boolean myOnKeyDown(int keyCode) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return false;
		}
		return true;
	}

	public void disableFooterView() {

		if (this.popupWindow.isShowing()) {
			this.popupWindow.dismiss();
			this.emoticonsButton.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_smiley));
		}
	}

	@Override
	public void keyClickedIndex(final String index) {
		Spanned cs = imageGetterSpanned(index);

		int cursorPosition = content.getSelectionStart();
		content.getText().insert(cursorPosition, cs);

	}

	public Spanned imageGetterSpanned(final String index) {
		ImageGetter imageGetter = new ImageGetter() {
			public Drawable getDrawable(String source) {
				StringTokenizer st = new StringTokenizer(index, ".");
				Drawable d = new BitmapDrawable(getResources(),
						emoticons[Integer.parseInt(st.nextToken()) - 1]);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};

		Spanned cs = Html.fromHtml("<img src ='" + index + "' alt='test'/>",
				imageGetter, null);

		return cs;

	}

	public class MessageListenerImpl implements MessageListener,
			ChatStateListener {

		@Override
		public void processMessage(Chat arg0, Message arg1) {
			System.out.println("Received message: " + arg1);

		}

		@Override
		public void stateChanged(Chat arg0, ChatState arg1) {
			if (ChatState.composing.equals(arg1)) {
				Log.d("Chat State", arg0.getParticipant() + " is typing..");
			} else if (ChatState.gone.equals(arg1)) {
				Log.d("Chat State", arg0.getParticipant()
						+ " has left the conversation.");
			} else {
				Log.d("Chat State", arg0.getParticipant() + ": " + arg1.name());
			}

		}

	}

	public class SmackSendReceiveFiles extends
			AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (params[0] == "send") {
				Spanned sentMessage = sendFile("");
				// call update chat history here
				// updateChatHistory(sentMessage, null);
			} else {
				receiveFile();
				// Spanned receivedMessage = receiveFile();
				// updateChatHistory(null, receivedMessage);
			}
			return false;
		}

		protected void onProgressUpdate(String... progress) {
			Log.d("UploadActivity", progress[0]);

		}

		@Override
		protected void onPostExecute(final Boolean success) {

		}
	}
}