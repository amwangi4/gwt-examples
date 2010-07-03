package org.gonevertical.core.client.ui.login;

import org.gonevertical.core.client.ClientPersistence;
import org.gonevertical.core.client.global.EventManager;
import org.gonevertical.core.client.global.Global_String;
import org.gonevertical.core.client.global.LoadingWidget;
import org.gonevertical.core.client.global.QueryString;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * TODO - add forgot username
 * TODO - setup forgot password emailing new password
 * TODO - setup email verification on new account
 * 
 * @author branflake2267
 *
 */
public class LoginUiInputs extends Composite implements 
MouseOverHandler, MouseOutHandler, ClickHandler, FocusHandler, BlurHandler, ChangeHandler {

	private ClientPersistence cp = null;

	// main widget div
	private FocusPanel pWidget = new FocusPanel();

	// remember me, create ...
	private HorizontalPanel pOptions = new HorizontalPanel();

	// used for a username
	private TextBox tbConsumerKey = new TextBox();

	// used for a password
	private TextBox tbConsumerSecret = new TextBox();
	private PasswordTextBox tbConsumerSecretPass = new PasswordTextBox();

	// remember me option
	private CheckBox cbRemberMe = new CheckBox();

	// TODO - move this to a floating overlay below the input boxes
	private VerticalPanel pError = new VerticalPanel(); 

	// loading Notification
	private LoadingWidget wLoading = new LoadingWidget();

	// put the user interface items here
	private	FlowPanel pUi = new FlowPanel();

	// login button
	private PushButton bLogin = new PushButton("Sign in");

	// forgot password, ask for it
	private PushButton bForgotPassword = new PushButton("Get Password");

	private Hyperlink hAccount = new Hyperlink("My Account", "core_account_aboutme");
	private Hyperlink hAccountCreate = new Hyperlink("Create Account", "core_account_create");

	// lets not change the url for these
	private Anchor aForgotUsername = new Anchor("Forgot Username");
	private Anchor aForgotPassword = new Anchor("Forgot Password");
	private Anchor aLoginLink = new Anchor("Sign in");
	private Anchor aLogout = new Anchor("Sign out");


	// true, when logged in, false, when not
	// this will allow to apply logic off this
	private boolean loginStatus = false;

	// input box labels
	private String inputLabel_ConsumerKey = "";
	private String inputLabel_ConsumerSecret = "Password";

	private boolean hoverOnOff = false;

	// set the inputs to horizontal or vertical
	private int uiType = LoginUi.LOGIN_HORIZONTAL;

	private boolean myLoginButtonWasClicked = false;

	/**
	 * constructor
	 */
	public LoginUiInputs(ClientPersistence cp, int uiType) {
		this.cp = cp;
		this.uiType = uiType;
		
		bLogin.setWidth("50px");

		inputLabel_ConsumerKey = cp.getInputLabel_ConsumerKey();

		initWidget(pWidget);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(wLoading);
		hp.add(new HTML("&nbsp;"));
		hp.add(pUi);

		pWidget.add(hp);

		// defaults
		bForgotPassword.setVisible(false);
		pError.setVisible(false);

		pWidget.addMouseOverHandler(this);
		pWidget.addMouseOutHandler(this);
		tbConsumerKey.addMouseOverHandler(this);
		tbConsumerKey.addMouseOutHandler(this);
		tbConsumerSecret.addMouseOverHandler(this);
		tbConsumerSecret.addMouseOutHandler(this);
		tbConsumerSecretPass.addMouseOverHandler(this);
		tbConsumerSecretPass.addMouseOutHandler(this);
		
		// TODO
		hAccountCreate.addClickHandler(this);
		hAccount.addClickHandler(this);


		tbConsumerKey.addClickHandler(this);
		tbConsumerKey.addFocusHandler(this);
		tbConsumerKey.addBlurHandler(this);
		tbConsumerSecret.addClickHandler(this);
		tbConsumerSecret.addFocusHandler(this);
		tbConsumerSecret.addBlurHandler(this);
		tbConsumerSecretPass.addClickHandler(this);
		tbConsumerSecretPass.addFocusHandler(this);
		tbConsumerSecretPass.addBlurHandler(this);

		aLogout.addClickHandler(this);
		bLogin.addClickHandler(this);
		bForgotPassword.addClickHandler(this);		
		cbRemberMe.addClickHandler(this);
		aLoginLink.addClickHandler(this);
		aForgotUsername.addClickHandler(this);
		aForgotPassword.addClickHandler(this);
		aLogout.addClickHandler(this);

		// style
		pWidget.setStyleName("core-login-ui");
		pError.setStyleName("core-Notification");
		hp.setCellVerticalAlignment(wLoading, VerticalPanel.ALIGN_MIDDLE);
		

		// observe global login events, and change state accordingly
		if (cp != null) {
  		cp.addChangeHandler(new ChangeHandler() {
  			public void onChange(ChangeEvent event) {
  				ClientPersistence cp = (ClientPersistence) event.getSource();
  				
  				if (cp.getChangeEvent() == EventManager.APPLICATION_LOADED) {
  					draw();
  					
  				} else if (cp.getChangeEvent() == EventManager.NEW_USER_CREATED) {
  					// nothing to do
  
  				} else if (cp.getChangeEvent() == EventManager.USER_LOGGEDIN) {
  					setLoginStatus(true);
  
  				} else if (cp.getChangeEvent() == EventManager.USER_LOGGEDOUT) {
  					setLoginStatus(false);
  
  				} else if (cp.getChangeEvent() == EventManager.LOGIN_DEMO) {
  					cp.resetEvent();
  					setDemoLogin();
  					
  				} 
  			}
  		});
		}

	}

	public void draw() {

		if (loginStatus == false) {
			tbConsumerKey.setText("");
			tbConsumerSecretPass.setText("");
			drawInputs();

		} else if (loginStatus == true) {
			drawLoggedIn();
		}

	}

	private void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
		draw();
	}

	/**
	 * for testing loggin
	 */
	private void setDemoLogin() {
		
		tbConsumerKey.setText("demo_user");
		tbConsumerSecret.setFocus(true);
		tbConsumerSecretPass.setText("password");
		
		Timer t = new Timer() {
			public void run() {
				startLogin();
			}
		};
		t.schedule(1500);
	}

	private void drawInputs() {
		if (uiType == LoginUi.LOGIN_HORIZONTAL) {
			drawInputs_Horizontal();

		} else if (uiType == LoginUi.LOGIN_VERTICAL) {
			drawInputs_Vertical();
		}
	}

	private void drawInputs_Horizontal() {

		//default
		tbConsumerSecret.setVisible(true);
		tbConsumerSecretPass.setVisible(false);

		// reset ui
		clear();

		cbRemberMe.setText("Remember Me");

		tbConsumerKey.setTitle(inputLabel_ConsumerKey);
		tbConsumerSecret.setTitle(inputLabel_ConsumerSecret);
		tbConsumerSecretPass.setTitle(inputLabel_ConsumerSecret);

		// hide loading by default
		hideLoading();

		// main login inputs
		HorizontalPanel ploginItems = new HorizontalPanel();
		ploginItems.setWidth("100%");
		ploginItems.add(tbConsumerKey);
		ploginItems.add(tbConsumerSecret);
		ploginItems.add(tbConsumerSecretPass);
		ploginItems.add(bLogin);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(ploginItems);

		pUi.add(vp);

		drawInputLabel_key();
		drawInputLabel_secret();

		ploginItems.setCellHorizontalAlignment(bLogin, HorizontalPanel.ALIGN_CENTER);
		tbConsumerKey.addStyleName("core-login-ui-inputconsumerkey");
		tbConsumerSecret.addStyleName("core-login-ui-inputconsumersecret");
		tbConsumerSecretPass.addStyleName("core-login-ui-inputconsumersecret");
		pOptions.setCellHorizontalAlignment(aForgotUsername, HorizontalPanel.ALIGN_RIGHT);
		pOptions.setCellHorizontalAlignment(aForgotPassword, HorizontalPanel.ALIGN_RIGHT);
		pOptions.setCellHorizontalAlignment(hAccountCreate, HorizontalPanel.ALIGN_RIGHT);
		pOptions.setCellVerticalAlignment(aForgotPassword, VerticalPanel.ALIGN_BOTTOM);
		pOptions.setCellVerticalAlignment(hAccountCreate, VerticalPanel.ALIGN_BOTTOM);
		pOptions.addStyleName("core-login-ui-inputoptions");
		pOptions.setWidth("100%");

		// login options
		initOptionsPanel();
		initErrorPanel();


		//vp.addStyleName("test1");
		//pOptions.addStyleName("test2");
	}

	private void drawInputs_Vertical() {

		//default
		tbConsumerSecret.setVisible(true);
		tbConsumerSecretPass.setVisible(false);

		// reset ui
		clear();

		cbRemberMe.setText("Remember Me");

		tbConsumerKey.setTitle(inputLabel_ConsumerKey);
		tbConsumerSecret.setTitle(inputLabel_ConsumerSecret);
		tbConsumerSecretPass.setTitle(inputLabel_ConsumerSecret);

		// hide loading by default
		hideLoading();

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(bLogin);

		// main login inputs
		VerticalPanel ploginItems = new VerticalPanel();
		ploginItems.setWidth("100%");
		ploginItems.add(tbConsumerKey);
		ploginItems.add(tbConsumerSecret);
		ploginItems.add(tbConsumerSecretPass);
		ploginItems.add(hp);
		ploginItems.add(cbRemberMe);
		ploginItems.add(aForgotUsername);
		ploginItems.add(aForgotPassword);
		ploginItems.add(hAccountCreate);


		tbConsumerKey.setWidth("200px");
		tbConsumerSecret.setWidth("200px");
		tbConsumerSecretPass.setWidth("200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(ploginItems);

		pUi.add(vp);

		drawInputLabel_key();
		drawInputLabel_secret();

		tbConsumerKey.addStyleName("core-login-ui-inputconsumerkey");
		tbConsumerSecret.addStyleName("core-login-ui-inputconsumersecret");
		tbConsumerSecretPass.addStyleName("core-login-ui-inputconsumersecret");
		pOptions.setCellHorizontalAlignment(aForgotUsername, HorizontalPanel.ALIGN_RIGHT);
		pOptions.setCellHorizontalAlignment(aForgotPassword, HorizontalPanel.ALIGN_RIGHT);
		pOptions.setCellHorizontalAlignment(hAccountCreate, HorizontalPanel.ALIGN_RIGHT);
		pOptions.setCellVerticalAlignment(aForgotUsername, VerticalPanel.ALIGN_BOTTOM);
		pOptions.setCellVerticalAlignment(aForgotPassword, VerticalPanel.ALIGN_BOTTOM);
		pOptions.setCellVerticalAlignment(hAccountCreate, VerticalPanel.ALIGN_BOTTOM);
		pOptions.addStyleName("core-login-ui-inputoptions");
		pOptions.setWidth("100%");

		// login options
		initErrorPanel();

	}

	private void initOptionsPanel() {
		pOptions.clear();
		pOptions.add(cbRemberMe);
		pOptions.add(aForgotPassword);
		pOptions.add(hAccountCreate);
		pOptions.setVisible(false);
		RootPanel.get().add(pOptions);

		pOptions.addStyleName("core-login-ui-options");
	}

	private void initErrorPanel() {
		pError.setVisible(false);
		RootPanel.get().add(pError);
	}

	private void displayOptions(boolean b) {
		if (loginStatus == true) {
			return;
		}
		if (bForgotPassword.isVisible() == true) {
			return;
		}
		if (pOptions == null) {
			return;
		}
		if (pOptions.isAttached() == false) {
			return;
		}

		int left = pWidget.getAbsoluteLeft();
		int top = pWidget.getAbsoluteTop() + pWidget.getOffsetHeight();
		int width = pWidget.getOffsetWidth();
		pOptions.setWidth(""+width+"px");
		RootPanel.get().setWidgetPosition(pOptions, left, top);

		if (b == true) {
			hoverOnOff = true;
			pOptions.setVisible(true);
		} else if (b == false) {
			hoverOnOff = false;
		}

		hideOptionsSlowly();

	}

	private void hideOptionsSlowly() {
		if (hoverOnOff == true) {
			return;
		}
		Timer t = new Timer() {
			public void run() {
				if (hoverOnOff == false) {
					pOptions.setVisible(false);
				}
			}
		};
		t.schedule(3000);
	}

	public void drawForgotPassword() {

		// reset ui
		clear();

		bForgotPassword.setTitle("This will reset your password, " +
		"and send you a email of the new password to login.");

		// tells the options not to show this way
		bForgotPassword.setVisible(true);

		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(4);
		hp.add(tbConsumerKey);
		hp.add(bForgotPassword);
		hp.add(aLoginLink);

		pUi.add(hp);

		// style
		aForgotPassword.addStyleName("core-login-ui-inputoptions");
		hp.setCellVerticalAlignment(aLoginLink, VerticalPanel.ALIGN_BOTTOM);
	}
	
	public void drawForgotUsername() {
		Window.alert("Coming soon...");
  }


	private void drawLoggedIn() {
		hideLoading();

		/* dont do this hear, b/c we may want it again, revisit later
		if (uiType == LoginUi.LOGIN_VERTICAL) {
			this.setVisible(false);
			return;
		}
		*/

		clear();

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(hAccount);
		hp.add(new HTML("&nbsp;&nbsp;"));
		hp.add(aLogout);

		pUi.add(hp);

		pOptions.setVisible(false);

	}

	private void clear() {
		// I use the bForgot to tell the options not to show up
		bForgotPassword.setVisible(false);
		pUi.clear();
	}

	public void drawError(String error) {
		if (error == null) {
			return;
		}
		
		if (pOptions.isAttached() == false) {
			return;
		}
		
		int left = pWidget.getAbsoluteLeft();
		int top = pWidget.getAbsoluteTop() + pWidget.getOffsetHeight();
		int width = pWidget.getOffsetWidth();
		pOptions.setWidth(""+width+"px");
		RootPanel.get().setWidgetPosition(pError, left, top);

		hideLoading();

		pError.clear();
		pError.setVisible(true);

		error = Global_String.removeHtmlTags(error);
		HTML htmlError = new HTML(error);

		pError.add(htmlError);

		// hide error shortly after fire
		// TODO - this has some multiple fires going, this needs to have a cancel system - later 
		Timer t = new Timer() {
			public void run() {
				pError.setVisible(false);
			}
		};
		t.schedule(4000);

		// reset password
		resetInputSecret();
	}

	private void resetInputSecret() {
		tbConsumerSecretPass.setText("");
		changePasswordInput_Focus();
	}

	private void drawInputLabel_key() {
		tbConsumerKey.setText(inputLabel_ConsumerKey);
		tbConsumerKey.addStyleName("core-login-ui-inputlabel");
	}

	private void drawInputLabel_secret() {
		tbConsumerSecret.setText(inputLabel_ConsumerSecret);
		tbConsumerSecret.addStyleName("core-login-ui-inputlabel");
	}

	protected String getConsumerKey() {
		String consumerKey = tbConsumerKey.getText();
		consumerKey = Global_String.removeHtmlTags(consumerKey).trim();
		return consumerKey; 
	}

	protected String getConsumerSecret() {
		String consumerSecret = tbConsumerSecretPass.getText();
		consumerSecret = Global_String.removeHtmlTags(consumerSecret).trim();
		return consumerSecret;
	}

	private void drawLoading() {
		wLoading.show();

		tbConsumerKey.setEnabled(false);
		tbConsumerSecretPass.setEnabled(false);
	}

	private void hideLoading() {
		wLoading.hide();

		tbConsumerKey.setEnabled(true);
		tbConsumerSecretPass.setEnabled(false);
	}

	public boolean getRememberMe() {
		return cbRemberMe.getValue();
	}

	/**
	 * change input label "Email" username
	 * 
	 * @param focus
	 */
	private void changeUsernameInput(boolean focus) {

		if (getConsumerKey().length() == 0 && focus == false) {
			tbConsumerKey.setText(inputLabel_ConsumerKey);
			tbConsumerKey.addStyleName("core-login-ui-inputlabel");

		} else if (getConsumerKey().equals(inputLabel_ConsumerKey) == true) {
			if (getConsumerKey().length() == 0 || getConsumerKey().equals(inputLabel_ConsumerKey) == true) {
				tbConsumerKey.setText("");
			}
			tbConsumerKey.removeStyleName("core-login-ui-inputlabel");
		}
	}

	/**
	 * change password to the intput mode 
	 */
	private void changePasswordInput_Focus() {

		tbConsumerSecret.setVisible(false);

		tbConsumerSecretPass.setEnabled(true);
		tbConsumerSecretPass.setVisible(true);

		tbConsumerSecretPass.setFocus(true);
	}

	/**
	 * change password to ready mode "Password"
	 */
	private void changePasswordInput_Blur() {
		if (getConsumerSecret().length() == 0 ) {
			tbConsumerSecret.setVisible(true);
			tbConsumerSecretPass.setVisible(false);
		} 
	}

	private void startLogin() {
		myLoginButtonWasClicked = true;
		drawLoading();
		cp.fireChange(EventManager.LOGINBUTTONCLICKED);
	}

	public void onClick(ClickEvent event) {

		Widget sender = (Widget) event.getSource();
		if (sender == bLogin) {
			startLogin();

		} else if (sender == bForgotPassword) {
			drawLoading();
			cp.fireChange(EventManager.FORGOT_PASSWORD);

		} else if (sender == tbConsumerKey) {
			changeUsernameInput(true);

		} else if (sender == tbConsumerSecret) {
			changePasswordInput_Focus();

		} else if (sender == cbRemberMe) {
			// TODO - save a cookie

		} else if (sender == aForgotPassword) {
			drawForgotPassword();

		} else if (sender == aForgotUsername) {
			drawForgotUsername();

		} else if (sender == aLoginLink) {
			drawInputs();

		} else if (sender == hAccountCreate) {
			if (QueryString.getQueryStringData().getHistoryToken().equals("core_create")) {
				cp.fireChange(EventManager.ACCOUNT_CREATE);
			}

		} else if (sender == hAccount) {
			if (QueryString.getQueryStringData().getHistoryToken().equals("core_account")) {
				cp.fireChange(EventManager.ACCOUNT);
			}

		} else if (sender == aLogout) {
			cp.fireChange(EventManager.LOGOUT);
		} 

	}
	
	public void onMouseOver(MouseOverEvent event) {

		Widget sender = (Widget) event.getSource();

		if (sender == pWidget) {
			displayOptions(true);
		}

	}

	public void onMouseOut(MouseOutEvent event) {

		Widget sender = (Widget) event.getSource();

		if (sender == pWidget) {
			displayOptions(false);
		}

	}

	public void onFocus(FocusEvent event) {

		Widget sender = (Widget) event.getSource();

		if (sender == tbConsumerKey) {


		} else if (sender == tbConsumerSecret) {
			changePasswordInput_Focus(); // change to input mode

		} else if (sender == tbConsumerSecretPass) {

		}

	}

	public void onBlur(BlurEvent event) {

		Widget sender = (Widget) event.getSource();

		if (sender == tbConsumerKey) {
			changeUsernameInput(false);

		} else if (sender == tbConsumerSecret) {

		}  else if (sender == tbConsumerSecretPass) {
			changePasswordInput_Blur();

		} else if (sender == cbRemberMe) {

		}

	}

	public void onChange(ChangeEvent event) {

		Widget sender = (Widget) event.getSource();

		if (sender == tbConsumerKey) {
			changeUsernameInput(false);

		} else if (sender == tbConsumerSecret) {
			// TODO

		} else if (sender == tbConsumerSecretPass) {
			changePasswordInput_Blur();

		} else if (sender == cbRemberMe) {
			// TODO

		} 
	}



}
