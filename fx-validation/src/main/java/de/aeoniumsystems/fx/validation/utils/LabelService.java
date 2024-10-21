/*
 * Copyright (C) 2024 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.aeoniumsystems.fx.validation.utils;

import de.aeoniumsystems.fx.validation.FXValidatorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;

/**
 * The label service provides a lookup map for labels placed in several scenes that are assigned to controls via
 * labelFor attribute. The label service is also used by the validator service.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 * @version 1.1
 */
public class LabelService {

  private static final Map<Node, List<Label>> LABELS_FOR_NODE_MAP = new HashMap<>();
  private static final Map<Label, Scene> SCENE_TO_LABEL_MAP = new HashMap<>();
  private static final Map<Scene, List<Node>> NODES_TO_SCENE_MAP = new HashMap<>();

  /**
   * This map keeps label lists for the scenes.
   */
  private static final Map<Scene, List<Label>> LABELS_FOR_SCENE_MAP = new HashMap<>();

  /**
   * Find the first label that is a label for this control.
   *
   * @param node The node to search the label for.
   * @return The first Label mapped to the node, or null
   * @deprecated Use getLabelsFor() for retrieving <b>all</b> Labels that may reference the node.
   */
  @Deprecated
  public static Label getLabelFor(Node node) {
    List<Label> list = LABELS_FOR_NODE_MAP.get(node);
    if (list != null) {
      if (!list.isEmpty()) {
        return list.get(0);
      }
      return null;
    } else {
      return null;
    }
  }

  /**
   * Get all labels that reference the given node.
   *
   * @param node The node to search labels for.
   * @return A list of labels that reference the node, may return <code>null</code>.
   */
  public static List<Label> getLabelsFor(Node node) {
    return LABELS_FOR_NODE_MAP.get(node);
  }

  /**
   * Initialize the labels in the given scene, i.e.,:
   * <ul>
   * <li>find all labels in the scene, </li>
   * <li>loop over the list of found labels and check for a 'Label for'-reference, </li>
   * <li>and, if found, put it in the internal map, set cursor style and add a click handler that lets the user focus
   * the node by clicking it's label.</li>
   * <li></li>
   * </ul>
   *
   * @param scene The scene to scan
   */
  public static void initialize(Scene scene) {
    initialize(scene.getRoot());
  }

  /**
   * Initialize all labels that are registered as label for a node.
   *
   * @since 1.1 Bind label's disable property to disabled property of node.
   * @see #initialize(Scene scene)
   * @param root The root node to scan from
   */
  public static void initialize(Parent root) {
    Scene scene = root.getScene();
    List<Node> list = NODES_TO_SCENE_MAP.get(scene);
    if (list == null) {
      list = new ArrayList<>();
      NODES_TO_SCENE_MAP.put(scene, list);
    }

    findLabels(scene, root.getChildrenUnmodifiable(), list);

    List<Label> labelList = LABELS_FOR_SCENE_MAP.get(scene);
    for (Label label : labelList) {
      if (label.getStyleClass().contains(FXValidatorService.AEFX_VALIDATION_MSG)) {
        label.setVisible(false);
        label.setManaged(false);
      }
      final Node labelFor = label.getLabelFor();
      if (labelFor != null) {

        List<Label> nodelabelList = LABELS_FOR_NODE_MAP.get(labelFor);
        if (nodelabelList == null) {
          nodelabelList = new ArrayList<>();
          LABELS_FOR_NODE_MAP.put(labelFor, nodelabelList);
        }
        nodelabelList.add(label);

        label.setCursor(Cursor.HAND);
        label.disableProperty().bind(labelFor.disabledProperty());
        label.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
          labelFor.requestFocus();
        });
      }
    }
  }

  /**
   * Iterate through the children and their children, and collect all label nodes in the internal labels map. The method
   * checks whether a label is already an element of the list and inserts the label only once. Hence, the method can be
   * executed several times on the same scene/node.
   *
   * <p>
   * Special treatment is required in this method for TabPane, since the Tab instances are not elements or the children
   * collection. </p>
   *
   * @param children List of child nodes
   * @param list .
   */
  private static void findLabels(ObservableList<Node> children, List<Node> list) {
    for (Node node : children) {
      if (node instanceof Label) {
        Label label = (Label) node;
        final Scene scene = getSceneOf(label);
        collectLabelInSceneMap(scene, label);
      }

      if (node instanceof Parent) {
        Parent parent = (Parent) node;
        findLabels(parent.getChildrenUnmodifiable(), list);
      }

      if (node instanceof TabPane) {
        TabPane tabPane = (TabPane) node;

        for (Tab tab : tabPane.getTabs()) {
          if (tab.getContent() instanceof Parent) {
            Parent p = (Parent) tab.getContent();
            findLabels(p.getChildrenUnmodifiable(), list);
          }
        }
      } else if (node instanceof TitledPane) {
        TitledPane titledPane = (TitledPane) node;
        final Node content = titledPane.getContent();
        if (content instanceof Parent) {
          Parent parent = (Parent) content;

          findLabels(parent.getChildrenUnmodifiable(), list);
        }
      }
    }
  }

  private static void collectLabelInSceneMap(final Scene scene, Label label) {
    List<Label> labelList = LABELS_FOR_SCENE_MAP.get(scene);
    if (labelList == null) {
      labelList = new ArrayList<>();
      LABELS_FOR_SCENE_MAP.put(scene, labelList);
    }

    if (!labelList.contains(label)) {
      labelList.add(label);
    }
  }

  /**
   * A variant of findLabels that tracks the scene in the parameter list - normally each node in the scene should know
   * it's scene, but ist seems like node in a TitledPane does not.
   *
   * @param scene The current scene
   * @param children Observable list of child nodes
   * @param list .
   */
  private static void findLabels(Scene scene, ObservableList<Node> children, List<Node> list) {
    for (Node node : children) {
      if (node instanceof Label) {
        Label label = (Label) node;
        collectLabelInSceneMap(scene, label);
      }

      if (node instanceof Parent) {
        Parent parent = (Parent) node;
        findLabels(scene, parent.getChildrenUnmodifiable(), list);
      }

      if (node instanceof TabPane) {
        TabPane tabPane = (TabPane) node;

        for (Tab tab : tabPane.getTabs()) {
          if (tab.getContent() instanceof Parent) {
            Parent p = (Parent) tab.getContent();
            findLabels(scene, p.getChildrenUnmodifiable(), list);
          }
        }
      } else if (node instanceof TitledPane) {
        TitledPane titledPane = (TitledPane) node;
        final Node content = titledPane.getContent();
        if (content instanceof Parent) {
          Parent parent = (Parent) content;

          findLabels(scene, parent.getChildrenUnmodifiable(), list);
        }
      }
    }
  }

  private static Scene getSceneOf(Node node) {
    Scene scene = node.getScene();
    if (scene == null) {
      Parent parent = node.getParent();
      int n = 0;
      while (parent != null) {
        if (parent.getScene() != null) {
          return parent.getScene();
        }
        n++;
        parent = parent.getParent();
      }
    }
    return scene;
  }

}
