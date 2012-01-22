package com.gonevertical.client.views.peoplelist;

import com.gonevertical.client.app.ClientFactory;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface PeopleListView extends IsWidget {

  interface Presenter {
    void goTo(Place place);
    
    void setRunning(boolean running);
  }

  void setPresenter(Presenter presenter);
  
  void setClientFactory(ClientFactory clientFactory);
  
  void start();
}
