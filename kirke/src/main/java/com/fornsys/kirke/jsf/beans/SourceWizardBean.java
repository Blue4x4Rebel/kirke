/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fornsys.kirke.jsf.beans;

import com.fornsys.kirke.model.Field;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.servlet.http.Part;

/**
 *
 * @author josh
 */
@ManagedBean
@SessionScoped
public class SourceWizardBean implements Serializable {

    private Part file;
    private HtmlPanelGroup dataTableGroup;
    private UIComponent formComponent;
    private FieldType fieldType;
    private char delimiter;
    private List<Field> fields;
    private int currentFieldIdx;
    private boolean headerRow;
    
    /**
     * Creates a new instance of SourceWizardBean
     */
    public SourceWizardBean() {
        dataTableGroup = new HtmlPanelGroup();
        dataTableGroup.getChildren().clear();
        formComponent = new HtmlPanelGroup();
        loadCompositeComponent(formComponent, "ezcomp", "fileUploadView.xhtml", "fileUploadView");
    }
    
    public void handleFileUpload() {
        if( file == null ) return;
        try {
            String firstLine = new BufferedReader(
                    new InputStreamReader(file.getInputStream()))
                .readLine();
            HtmlOutputText flText = new HtmlOutputText();
            flText.setValue(firstLine);

            this.dataTableGroup.getChildren().add(flText);
            loadCompositeComponent(formComponent, "ezcomp", "fileProperties.xhtml", "fileProperties");
        } catch (IOException e) {
          // Error handling
        }
    }
    
    public void finishFileProperties() throws IOException {
        // TODO: Validate file properties....
        fields = new ArrayList<>();
        if( fieldType == FieldType.delimited ) {
            String firstLine = new BufferedReader(
                new InputStreamReader(file.getInputStream()))
                    .readLine();
            Scanner sc = new Scanner(firstLine).useDelimiter(Character.toString(delimiter));
            while(sc.hasNext()) {
                String name = sc.next();
                fields.add(new Field(isHeaderRow() ? name : null));
            }
        } else {
            fields.add(new Field());
        }
        currentFieldIdx = 0;
        loadCompositeComponent(formComponent, "ezcomp", "fieldProperties.xhtml", "fieldProperties"+currentFieldIdx);
    }
    
    private void generateTableFromColumnList(List<Field> fields) {
        
    }

    public static void loadCompositeComponent(UIComponent parent, String libraryName, String resourceName, String id) {
        // Prepare.
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        FaceletContext faceletContext = (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);

        // This basically creates <ui:component> based on <composite:interface>.
        Resource resource = application.getResourceHandler().createResource(resourceName, libraryName);
        UIComponent composite = application.createComponent(context, resource);
        composite.setId(id); // Mandatory for the case composite is part of UIForm! Otherwise JSF can't find inputs.

        // This basically creates <composite:implementation>.
        UIComponent implementation = application.createComponent(UIPanel.COMPONENT_TYPE);
        implementation.setRendererType("javax.faces.Group");
        composite.getFacets().put(UIComponent.COMPOSITE_FACET_NAME, implementation);

        // Now include the composite component file in the given parent.
        parent.getChildren().clear();
        parent.getChildren().add(composite);
        parent.pushComponentToEL(context, composite); // This makes #{cc} available.
        try {
            faceletContext.includeFacelet(implementation, resource.getURL());
        } catch (IOException e) {
            throw new FacesException(e);
        } finally {
            parent.popComponentFromEL(context);
        }
    }

    /**
     * @return the file
     */
    public Part getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(Part file) {
        this.file = file;
    }
    
    /**
     * @return the dataTableGroup
     */
    public HtmlPanelGroup getDataTableGroup() {
        return dataTableGroup;
    }

    /**
     * @param dataTableGroup the dataTableGroup to set
     */
    public void setDataTableGroup(HtmlPanelGroup dataTableGroup) {
        this.dataTableGroup = dataTableGroup;
    }

    /**
     * @return the formComponent
     */
    public UIComponent getFormComponent() {
        return formComponent;
    }

    /**
     * @param formComponent the formComponent to set
     */
    public void setFormComponent(UIComponent formComponent) {
        this.formComponent = formComponent;
    }

    /**
     * @return the fieldType
     */
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return the delimiter
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @return the fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @return the currentFieldIdx
     */
    public int getCurrentFieldIdx() {
        return currentFieldIdx;
    }

    /**
     * @param currentFieldIdx the currentFieldIdx to set
     */
    public void setCurrentFieldIdx(int currentFieldIdx) {
        this.currentFieldIdx = currentFieldIdx;
    }

    /**
     * @return the headerRow
     */
    public boolean isHeaderRow() {
        return headerRow;
    }
    
    public boolean getHeaderRow() {
        return headerRow;
    }

    /**
     * @param headerRow the headerRow to set
     */
    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }
    
    public enum FieldType {
        delimited, fixed,
    }
}
