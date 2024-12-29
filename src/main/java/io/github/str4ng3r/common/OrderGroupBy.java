/*
 * The GPLv3 License (GPLv3)
 * 
 * Copyright (c) 2023 Pablo Eduardo Martinez Solis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.str4ng3r.common;

/**
 *
 * @author Pablo Eduardo Martinez Solis
 */
class OrderGroupBy {
  String orderBy;

  public void orderBy(String orderBy, boolean descending) {
    this.orderBy = descending ? orderBy + " DESC" : orderBy;
  }

  public void groupBy(String groupBy) {
    this.orderBy = groupBy;
  }

  public String write() {
    return orderBy;
  }
}
