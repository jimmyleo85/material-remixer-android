/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.libraries.remixer;

import java.lang.ref.WeakReference;

/**
 * An item that can be displayed on Remixer's interfaces.
 *
 * <p>These are either {@link Variable}es or {@link Trigger}s.
 */
public abstract class RemixerItem {

  /**
   * The name to display in the UI for this remixer item.
   */
  private final String title;
  /**
   * The key to use to identify this item across storage and all the interfaces.
   */
  private final String key;
  /**
   * The layout to inflate to display this remixer item. If set to 0, the default layout associated
   * with the remixer item type will be used.
   */
  private final int layoutId;
  /**
   * A weak reference to the object that created this RemixerItem.
   *
   * <p>It should be a reference to an activity, but it isn't since remixer_core cannot depend on
   * Android classes. It is a weak reference in order not to leak the activity accidentally.
   */
  @SuppressWarnings("unchecked")
  private final WeakReference parentObject;
  /**
   * A copy of the parent object's class object. This will be necessary to know whether an object
   * is of the same class as the parent object, even after the parent object has been reclaimed by
   * the Garbage Collector.
   */
  @SuppressWarnings("unchecked")
  private final Class parentObjectClass;
  /**
   * The remixer instance this RemixerItem has been attached to.
   */
  protected Remixer remixer;

  /**
   * Constructs a new RemixerItem with the given key, title and layoutId.
   */
  @SuppressWarnings("unchecked")
  protected RemixerItem(String title, String key, Object parentObject, int layoutId) {
    this.title = title;
    this.key = key;
    this.parentObject = new WeakReference(parentObject);
    this.parentObjectClass = parentObject.getClass();
    this.layoutId = layoutId;
  }

  /**
   * Checks whether the parent object is the same as the parameter.
   */
  public boolean isParentObject(Object object) {
    if (object == null) {
      return false;
    }
    return parentObject.get() == object;
  }

  /**
   * Checks whether the parent object has been reclaimed.
   */
  public boolean hasParentObject() {
    return parentObject.get() != null;
  }

  /**
   * Checks whether the parameter is of the same class as the (possibly already reclaimed) parent
   * object.
   */
  public boolean isSameClassAsParentObject(Object object) {
    return parentObjectClass == object.getClass();
  }

  public String getTitle() {
    return title;
  }

  public String getKey() {
    return key;
  }

  /**
   * Returns the layout id to inflate when displaying this Remixer item.
   */
  public int getLayoutId() {
    return layoutId;
  }

  /**
   * Removes the callback for this remixer item, it is used to avoid leaks through callbacks once
   * activities are destroyed.
   */
  abstract void clearCallback();

  /**
   * Checks whether {@code item} is compatible with this RemixerItem.
   * @throws IncompatibleRemixerItemsWithSameKeyException if {@code item} has the same key as this
   *     object, and they are of different types or otherwise incompatible.
   */
  abstract void assertIsCompatibleWith(RemixerItem item);

  /**
   * Returns the parent object.
   */
  Object getParentObject() {
    return parentObject.get();
  }

  /**
   * Clears the parent object reference to simulate reclaiming the parent object in tests.
   *
   * <p><b>Visible only for testing.</b>
   */
  void clearParentObject() {
    parentObject.clear();
  }

  /**
   * Set the current remixer instance. This allows the Remixer item to notify other items with the
   * same key.
   */
  public void setRemixer(Remixer remixer) {
    this.remixer = remixer;
  }
}
