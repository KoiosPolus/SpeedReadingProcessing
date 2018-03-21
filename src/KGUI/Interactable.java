package KGUI;

public abstract class Interactable extends Component {
    abstract boolean changeState();
    int state = 0, states;

    void registerClick() {
        if (gui.activeElement == this && !gui.editMode) {
            if (this.changeState()) {
                System.out.println(this + " firing");
                state++;
                state %= states;
            }
            gui.activeElement = null;
        }
    }
}
