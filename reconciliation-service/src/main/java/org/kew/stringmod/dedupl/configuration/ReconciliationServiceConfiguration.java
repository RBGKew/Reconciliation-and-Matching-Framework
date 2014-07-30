package org.kew.stringmod.dedupl.configuration;

import java.util.List;

import org.kew.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.refine.domain.metadata.MetadataPreview;
import org.kew.reconciliation.refine.domain.metadata.MetadataSuggest;
import org.kew.reconciliation.refine.domain.metadata.MetadataSuggestDetails;
import org.kew.reconciliation.refine.domain.metadata.MetadataView;
import org.kew.reconciliation.refine.domain.metadata.Type;
import org.kew.reconciliation.service.resultformatter.ReconciliationResultFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

public class ReconciliationServiceConfiguration extends MatchConfiguration implements InitializingBean {

	private ApplicationContext context;
	private Metadata reconciliationServiceMetadata;

	private QueryStringToPropertiesExtractor queryStringToPropertiesExtractor;
	private ReconciliationResultFormatter reconciliationResultFormatter;

	private String description;

	// Metadata values
	private String title;
	private String identifierSpace;
	private String schemaSpace;
	private String viewUrl;
	private String previewUrl;
	private int previewWidth;
	private int previewHeight;
	private List<Type> defaultTypes;

	private String suggestFlyoutUrl;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Generate Metadata object
		reconciliationServiceMetadata = new Metadata();
		reconciliationServiceMetadata.setName(title);
		reconciliationServiceMetadata.setIdentifierSpace(identifierSpace);
		reconciliationServiceMetadata.setSchemaSpace(schemaSpace);

		MetadataView metadataView = new MetadataView();
		metadataView.setUrl(viewUrl);
		reconciliationServiceMetadata.setView(metadataView);

		MetadataPreview metadataPreview = new MetadataPreview();
		metadataPreview.setUrl(previewUrl);
		metadataPreview.setWidth(previewWidth);
		metadataPreview.setHeight(previewHeight);
		reconciliationServiceMetadata.setPreview(metadataPreview);

		MetadataSuggestDetails metadataSuggestTypeDetails = new MetadataSuggestDetails();
		metadataSuggestTypeDetails.setService_url("LOCAL");
		metadataSuggestTypeDetails.setService_path("BASE/suggestType");
		metadataSuggestTypeDetails.setFlyout_service_url("LOCAL");
		metadataSuggestTypeDetails.setFlyout_service_path("BASE/flyoutType/${id}");

		MetadataSuggestDetails metadataSuggestPropertyDetails = new MetadataSuggestDetails();
		metadataSuggestPropertyDetails.setService_url("LOCAL");
		metadataSuggestPropertyDetails.setService_path("BASE/suggestProperty");
		metadataSuggestPropertyDetails.setFlyout_service_url("LOCAL");
		metadataSuggestPropertyDetails.setFlyout_service_path("BASE/flyoutProperty/${id}");

		MetadataSuggestDetails metadataSuggestEntityDetails = new MetadataSuggestDetails();
		metadataSuggestEntityDetails.setService_url("LOCAL");
		metadataSuggestEntityDetails.setService_path("BASE");
		metadataSuggestEntityDetails.setFlyout_service_url("LOCAL");
		metadataSuggestEntityDetails.setFlyout_service_path("BASE/flyout/${id}");

		MetadataSuggest metadataSuggest = new MetadataSuggest();
		metadataSuggest.setType(metadataSuggestTypeDetails);
		metadataSuggest.setProperty(metadataSuggestPropertyDetails);
		metadataSuggest.setEntity(metadataSuggestEntityDetails);

		reconciliationServiceMetadata.setSuggest(metadataSuggest);
		reconciliationServiceMetadata.setDefaultTypes(defaultTypes.toArray(new Type[defaultTypes.size()]));
	}

	/* ••• Getters and setters ••• */
	public ApplicationContext getContext() {
		return context;
	}
	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public Metadata getReconciliationServiceMetadata() {
		return reconciliationServiceMetadata;
	}
	protected void setReconciliationServiceMetadata(Metadata reconciliationServiceMetadata) {
		this.reconciliationServiceMetadata = reconciliationServiceMetadata;
	}

	public QueryStringToPropertiesExtractor getQueryStringToPropertiesExtractor() {
		return queryStringToPropertiesExtractor;
	}
	/**
	 * Turns a single query into a set of properties for matching.
	 */
	public void setQueryStringToPropertiesExtractor(QueryStringToPropertiesExtractor queryStringToPropertiesExtractor) {
		this.queryStringToPropertiesExtractor = queryStringToPropertiesExtractor;
	}

	public ReconciliationResultFormatter getReconciliationResultFormatter() {
		return reconciliationResultFormatter;
	}
	/**
	 * Formats results for JSON responses.
	 */
	public void setReconciliationResultFormatter(ReconciliationResultFormatter reconciliationResultFormatter) {
		this.reconciliationResultFormatter = reconciliationResultFormatter;
	}

	public String getDescription() {
		return description;
	}
	/**
	 * Longer description of the purpose of the service, for display etc.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}
	/**
	 * The Metadata service title for the service.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentifierSpace() {
		return identifierSpace;
	}
	/**
	 * Metadata identifier space.
	 */
	public void setIdentifierSpace(String identifierSpace) {
		this.identifierSpace = identifierSpace;
	}

	public String getSchemaSpace() {
		return schemaSpace;
	}
	/**
	 * Metadata schema space.
	 */
	public void setSchemaSpace(String schemaSpace) {
		this.schemaSpace = schemaSpace;
	}

	public String getViewUrl() {
		return viewUrl;
	}
	/**
	 * URL to view web page for record.
	 */
	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}
	/**
	 * URL for preview web page, should be compact as it's shown in a popup.
	 */
	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public Integer getPreviewWidth() {
		return previewWidth;
	}
	/**
	 * Preview popup width.
	 */
	public void setPreviewWidth(Integer previewWidth) {
		this.previewWidth = previewWidth;
	}

	public Integer getPreviewHeight() {
		return previewHeight;
	}
	/**
	 * Preview popup height.
	 */
	public void setPreviewHeight(Integer previewHeight) {
		this.previewHeight = previewHeight;
	}

	public List<Type> getDefaultTypes() {
		return defaultTypes;
	}
	/**
	 * Default type(s) used by the service.
	 */
	public void setDefaultTypes(List<Type> defaultTypes) {
		this.defaultTypes = defaultTypes;
	}

	public String getSuggestFlyoutUrl() {
		return suggestFlyoutUrl;
	}
	/**
	 * URL template for suggest entity flyouts.  <code>{id}</code> will be replaced with the entity id.
	 */
	public void setSuggestFlyoutUrl(String suggestFlyoutUrl) {
		this.suggestFlyoutUrl = suggestFlyoutUrl;
	}
}
