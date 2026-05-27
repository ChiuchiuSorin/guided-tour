/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
import type { TourTask } from "./task";
import type { TourTaskStatus } from "./tourTaskStatus";

/**
 * Representation of a guidedtour Tour.
 *
 * @since 1.0
 * @beta
 */
export interface TourTour {
  /**
   * The pretty name of the task, to be used in the UI.
   */
  title: string;
  /**
   * The task id, from the backend.
   */
  id: string;
  /**
   * Status of the tour.
   */
  status?: TourTaskStatus;
  /**
   * Whether this Tour is completable by the user.
   */
  active?: boolean;
  /**
   * Whether this Tour is collapsed in the widget UI.
   */
  isCollapsed?: boolean;
  /**
   * The tasks of the tour, if already fetched. Otherwise, they should be fetched with the
   * GuidedTourManagerApi.getTasks(tourId) method.
   */
  tasksList?: TourTask[];
}
