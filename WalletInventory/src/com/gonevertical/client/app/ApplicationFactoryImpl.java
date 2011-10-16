package com.gonevertical.client.app;

import com.gonevertical.client.app.activity.ApplicationActivityMapper;
import com.gonevertical.client.app.activity.ApplicationPlaceHistoryMapper;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.requestfactory.ApplicationRequestFactory;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.gonevertical.client.views.SignInView;
import com.gonevertical.client.views.WalletEditView;
import com.gonevertical.client.views.WalletListView;
import com.gonevertical.client.views.impl.SignInViewImpl;
import com.gonevertical.client.views.impl.WalletEditViewImpl;
import com.gonevertical.client.views.impl.WalletListViewImpl;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ApplicationFactoryImpl implements ApplicationFactory {

  /**
   * default start point in application - if nothing is in url
   */
  private Place defaultPlace = new SignInPlace(null);
  
  /**
   * default shell for app (widget|container)
   */
  private SimplePanel appWidget = new SimplePanel();
  
  /**
   * for global events
   */
  private static final EventBus eventBus = new SimpleEventBus();
  
  /**
   * for datastore persistence calls
   */
  private final ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
  
  /**
   * place's directory
   */
  private final ActivityMapper activityMapper = new ApplicationActivityMapper(this);

  /**
   * place's director (internal flagger|flagman for places)
   */
  private final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);

  /**
   * URL tokenizer directory
   */
  private final ApplicationPlaceHistoryMapper historyMapper = GWT.create(ApplicationPlaceHistoryMapper.class);
  
  /**
   * URL tokenizer's director (URL event flagger|flagman)
   */
  private final PlaceHistoryHandler placeHistoryHandler = new PlaceHistoryHandler(historyMapper);
  
  /**
   * app place's director (internal flagger|flagman)
   */
  private final PlaceController placeController = new PlaceController(eventBus);
  
  /**
   * logged in user (or null user with url to login)
   */
  private UserDataProxy userData;
  
  
  /**
   * init
   */
  public ApplicationFactoryImpl() {
    
    requestFactory.initialize(eventBus);
    
    // shell for app
    activityManager.setDisplay(appWidget);
    
    // setup defaults
    placeHistoryHandler.register(placeController, eventBus, defaultPlace);
    
    
    RootPanel.get().add(appWidget);
    
    // Goes to the place represented on URL else default place
    placeHistoryHandler.handleCurrentHistory();
  }





  @Override
  public EventBus getEventBus() {
    return eventBus;
  }
  
  @Override
  public ApplicationRequestFactory getRequestFactory() {
    return requestFactory;
  }
  
  @Override
  public PlaceController getPlaceController() {
    return placeController;
  }

  @Override
  public void setUserData(UserDataProxy userData) {
    this.userData = userData;
    
    Auth auth = null;
    if (userData != null && userData.getId() != null && userData.getId() > 0) {
      auth = Auth.LOGGEDIN;
    } else {
      auth = Auth.LOGGEDOUT;
    }
    eventBus.fireEvent(new AuthEvent(auth, userData));
  }
  
  @Override 
  public UserDataProxy getUserData() {
    return userData;
  }


  @Override
  public ActivityManager getActivityManager() {
    return activityManager;
  }
  
  



}