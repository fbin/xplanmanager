package org.xplan.manager.web.client;

import java.util.Comparator;
import java.util.List;

import org.xplan.manager.web.shared.Plan;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class XPlanMgrWeb implements EntryPoint {

	private final XPlanMgrWebServiceAsync server = GWT.create(XPlanMgrWebService.class);

	CellTable<Plan> xplanTable = new CellTable<Plan>();
	ListDataProvider<Plan> dataProvider = new ListDataProvider<Plan>();
	DialogBox uploading;
	
	public Widget createFileUpload() {

		HorizontalPanel vPanel = new HorizontalPanel();
		
	    final FormPanel form = new FormPanel();
	    form.setAction(GWT.getModuleBaseURL() + "service");
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);

	    form.setWidget(vPanel);   
	    vPanel.add(new HTML("Plan hinzufügen: "));
	    final FileUpload upload = new FileUpload();
	    upload.setName("uploadFormElement");
	    vPanel.add(upload);

	    vPanel.add(new Button("Upload", new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        form.submit();
	        uploading = new DialogBox(false,true);
	        uploading.setText("Uploading File...");
	        uploading.center();
	        uploading.show();
	      }
	    }));

	    form.addSubmitHandler(new FormPanel.SubmitHandler() {
	      public void onSubmit(SubmitEvent event) {
	        if (upload.getFilename().length() == 0) {
	        	uploading.hide();
	        	Window.alert("Uploading failed!");
	        	event.cancel();
	        }
	      }
	    });
	    
	    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
	      public void onSubmitComplete(SubmitCompleteEvent event) {
	    	  uploading.hide();
	    	  reloadPlans();
	        Window.alert(event.getResults());
	      }
	    });
	
		return form;
	}
	
	Comparator<Plan> nameComparator = new Comparator<Plan>() {
    	@Override
    	public int compare(Plan o1, Plan o2) {
    		if (o1 == o2) return 0;
    		if (o1 != null) return (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
    		return -1;
    	}
    };
	Comparator<Plan> idComparator = new Comparator<Plan>() {
    	@Override
    	public int compare(Plan o1, Plan o2) {
    		if (o1 == o2) return 0;
    		if (o1 != null) return (o2 != null) ? o1.getId().compareTo(o2.getId()) : 1;
    		return -1;
    	}
    };
	Comparator<Plan> typeComparator = new Comparator<Plan>() {
    	@Override
    	public int compare(Plan o1, Plan o2) {
    		if (o1 == o2) return 0;
    		if (o1 != null) return (o2 != null) ? o1.getType().compareTo(o2.getType()) : 1;
    		return -1;
    	}
    };
	Comparator<Plan> loadComparator = new Comparator<Plan>() {
    	@Override
    	public int compare(Plan o1, Plan o2) {
    		if (o1 == o2) return 0;
    		if (o1 != null) return (o2 != null) ? o1.isLoaded().compareTo(o2.isLoaded()) : 1;
    		return -1;
    	}
    };
    Comparator<Plan> validComparator = new Comparator<Plan>() {
    	@Override
    	public int compare(Plan o1, Plan o2) {
    		if (o1 == o2) return 0;
    		if (o1 != null) return (o2 != null) ? o1.isValidated().compareTo(o2.isValidated()) : 1;
    		return -1;
    	}
    };
    
    void reloadPlans(){
		server.getPlans(
				new AsyncCallback<List<Plan>>() {
					public void onFailure(Throwable caught) {
						xplanTable.setRowCount(0,true);
						Window.alert("RPC Failure - gePlans() failed");
					}

					public void onSuccess(List<Plan> result) {
						xplanTable.setRowCount(result.size(),true);
					    //copy data to local list
					    List<Plan> list = dataProvider.getList();
					    list.clear();
					    list.addAll(result);

//					    if( sortColumn != null )
//					    	xplanTable.getColumnSortList().push(sortColumn);
					    ColumnSortEvent.fire(xplanTable, xplanTable.getColumnSortList());
					}
				});
    }
    
//TODO: replace reloadPlans() by getPlan()
    void removePlan(String id){
    	server.removePlan(id,
    			//event.g
    			new AsyncCallback<Integer>() {
    		public void onFailure(Throwable caught) {
    			reloadPlans();
    		}

    		public void onSuccess(Integer result) {
    			reloadPlans();
    		}
    	});
    }
    
    void loadPlan(String id){
    	server.loadPlan(id,
    			new AsyncCallback<Integer>() {
    		public void onFailure(Throwable caught) {
    			reloadPlans();
    		}

    		public void onSuccess(Integer result) {
    			reloadPlans();
    		}
    	});
    }
    
    void validatePlan(String id){
    	server.validatePlan(id,
    			new AsyncCallback<Integer>() {
    		public void onFailure(Throwable caught) {
    			reloadPlans();
    		}

    		public void onSuccess(Integer result) {
    			reloadPlans();
    		}
    	});
    }
    
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		
//--------------------------------------------------------------------------------------------
//Create a CellTable.
			xplanTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		    TextColumn<Plan> nameColumn = new TextColumn<Plan>() {
		      @Override
		      public String getValue(Plan object) {
		        return object.getName();
		      }
		    };
		    nameColumn.setSortable(true);
		    xplanTable.addColumn(nameColumn, "Name");

		    TextColumn<Plan> idColumn = new TextColumn<Plan>() {
		      @Override
		      public String getValue(Plan object) {
		        return object.getId();
		      }
		    };
		    idColumn.setSortable(true);
		    xplanTable.addColumn(idColumn, "Id");

		    TextColumn<Plan> typeColumn = new TextColumn<Plan>() {
		    	@Override
		    	public String getValue(Plan object) {
		    		return object.getType();
		    	}
		    };
		    typeColumn.setSortable(true);
		    xplanTable.addColumn(typeColumn, "Planart");

		    
		    ClickableTextCell loadedButtonCell = new ClickableTextCell();
		    Column<Plan, String> loadedColumn = new Column<Plan, String>(loadedButtonCell) {
		      @Override
		      public String getValue(Plan object) {
		        return object.isLoaded()?"+":"-";
		      } 
		      @Override
              public String getCellStyleNames(Context context, Plan  object) {
		    	  return object.isLoaded()?"buttonLoaded":"buttonNotLoaded";
              }  
		    };		  
		    loadedColumn.setFieldUpdater(new FieldUpdater<Plan, String>() {
		    	public void update(int index, Plan object, String value) {
		    		loadPlan(object.getId());
		    	}
		    });
		    loadedColumn.setSortable(true);
		    xplanTable.addColumn(loadedColumn, "Geladen");
		    
		    
		    ClickableTextCell validatedButtonCell = new ClickableTextCell();
		    Column<Plan, String> validatedColumn = new Column<Plan, String>(validatedButtonCell) {
		      @Override
		      public String getValue(Plan object) {
		        return object.isValidated()?"+":"-";
		      }
		      @Override
              public String getCellStyleNames(Context context, Plan  object) {
		    	  return object.isValidated()?"buttonLoaded":"buttonNotLoaded";
              }  
		    };		  
		    validatedColumn.setFieldUpdater(new FieldUpdater<Plan, String>() {
		    	public void update(int index, Plan object, String value) {
		    		validatePlan(object.getId());
		    	}
		    });
		    validatedColumn.setSortable(true);
		    xplanTable.addColumn(validatedColumn, "Validiert");
		    
		    ButtonCell previewButtonCell = new ButtonCell();
		    Column<Plan, String> previewButtonColumn= new Column<Plan, String>(previewButtonCell) {
		    	@Override
		    	public String getValue(Plan object) {
		    		return "K";
		    	}
		    };		  
		    previewButtonColumn.setFieldUpdater(new FieldUpdater<Plan, String>() {
		    	public void update(int index, Plan object, String value) {
		    		Window.alert("Kartenvorschau");
		    	}
		    });
		    xplanTable.addColumn(previewButtonColumn);
		    
		    ButtonCell buttonCell = new ButtonCell();
		    Column<Plan, String> buttonColumn = new Column<Plan, String>(buttonCell) {
		      @Override
		      public String getValue(Plan object) {
		        return "S";
		      }
		    };		  
		    buttonColumn.setFieldUpdater(new FieldUpdater<Plan, String>() {
		    	public void update(int index, Plan object, String value) {
		    		String url = GWT.getModuleBaseURL() + "service?plan=" + object.getId();
		    		Window.Location.assign(url);
		    	}
		    });
		    xplanTable.addColumn(buttonColumn,"Aktionen");

		    ButtonCell removeButtonCell = new ButtonCell();
		    Column<Plan, String> removeButtonColumn= new Column<Plan, String>(removeButtonCell) {
		      @Override
		      public String getValue(Plan object) {
		        return "X";
		      }
		    };		  
		    removeButtonColumn.setFieldUpdater(new FieldUpdater<Plan, String>() {
		    	public void update(int index, Plan object, String value) {
		    		if(Window.confirm("Wirklich Plan " + object.getName() + " entfernen?"))
		    			removePlan(object.getId());
		    	}
		    });
		    xplanTable.addColumn(removeButtonColumn);
		    
		    dataProvider.addDataDisplay(xplanTable);
		    
		    ListHandler<Plan> columnSortHandler = new ListHandler<Plan>(dataProvider.getList());
		    columnSortHandler.setComparator(xplanTable.getColumn(0),nameComparator);
		    columnSortHandler.setComparator(xplanTable.getColumn(1),idComparator);
		    columnSortHandler.setComparator(xplanTable.getColumn(2),typeComparator);
		    columnSortHandler.setComparator(xplanTable.getColumn(3),loadComparator);	
		    columnSortHandler.setComparator(xplanTable.getColumn(4),validComparator);
		    xplanTable.addColumnSortHandler(columnSortHandler);

		    // Add a selection model to handle user selection.
//		    final SingleSelectionModel<Plan> selectionModel = new SingleSelectionModel<Plan>();
//		    xplanTable.setSelectionModel(selectionModel);
//		    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//		      public void onSelectionChange(SelectionChangeEvent event) {
//		    	  Plan selected = selectionModel.getSelectedObject();
//		        if (selected != null) {
//		          Window.alert("You selected: " + selected.getName());
//		        }
//		      }
//		    });
		   
//----------------------------------------------------
		  
		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				//sendButton.setEnabled(true);
				//sendButton.setFocus(true);
			}
		});
		
		VerticalPanel mainPanel = new VerticalPanel();
		HorizontalPanel header = new HorizontalPanel();

		Button reloadPlansButton = new Button("Pläne neu laden");
		Button help = new Button("Hilfe");
		header.add(reloadPlansButton);
		header.add(help);
		
		mainPanel.setWidth("80%");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.add(createFileUpload());
		mainPanel.add(header);
	    mainPanel.add(xplanTable);
		VerticalPanel outerPanel = new VerticalPanel();
		outerPanel.setWidth("100%");
		outerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		outerPanel.add(mainPanel);
	    RootPanel.get().add(outerPanel);

	    reloadPlansButton.addClickHandler(new ClickHandler() {
	    	public void onClick(ClickEvent event) {
	    		reloadPlans();
	    	}
	    });
	    
	    ClickHandler removeHandler = new ClickHandler() {
	    	public void onClick(ClickEvent event) {

	    	}
	    };
		reloadPlansButton.addClickHandler(removeHandler);
	    
	    help.addClickHandler(new ClickHandler() {
	    	public void onClick(ClickEvent event) {
				dialogBox.setText("Help");
				dialogBox.center();
				closeButton.setFocus(true);
	    	}
	    });	
	    reloadPlans();
	}
}
