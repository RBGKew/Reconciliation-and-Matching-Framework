/**
 * This package contains the controllers for the matchconf-UI. You will notice
 * that most of them are called Custom*.java, the Custom one being the one used
 * as the original <Classname>Controller was created by Spring Roo and had to be
 * customized.
 *
 * As Roo (given a roo shell is running) automatically updates the controllers
 * after changes to the model it was convenient to keep the original
 * <Classname>Controller classes.
 *
 * There is not documentation on most of them as it was to boring to document each
 * delete, create, update, updateForm, show, list, populateEditForm,
 * encodeUrlPathSegment, .. They all repeat for each controller, read the
 * documentations for spring based web applications and spring roo.
 *
 * A major change I have made is to change the default id-based url-mapping to a
 * name- or slug-based one. This seems to be much more eye friendly.
 *
 * Sometimes I added 'deleteByid', which is a work-around to delete entities based on
 * their id rather than their name (this should be considered as deprecated now),
 * and 'customValidation' which is a form validation specific to each controller.
 */
package org.kew.stringmod.matchconf.web;
