/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
package org.kew.rmf.matchconf.web;
