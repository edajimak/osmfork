package net.osmand.plus.lrrp;

import net.osmand.plus.jmbe.codec.ambe.AMBEFrame;

import java.util.ArrayList;
import java.util.List;

public class MbeEvent {
    public int unix;
    public int tg;
    public int from;
    public LrrpPoint point;
    public List<AMBEFrame> frames = new ArrayList<>();
}
