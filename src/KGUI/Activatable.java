package KGUI;

import processing.core.PGraphics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import KGUI.Component;

public abstract class Activatable extends Component {
    Executable target = null;
}


//public abstract class Container extends Component {
//
//    protected Container() {
////        try {
////            Class<?> memberClass = Thread.currentThread().getContextClassLoader().loadClass("KGUI.DropDownTree");
////            Constructor<?> constructor = memberClass.getConstructor();
////            List<Component> MemberList =  new ArrayList<Component>(); //constructor.newInstance();
////        } catch (ClassNotFoundException | NoSuchMethodException e) {
////            e.printStackTrace();
////        }
//    }
//}