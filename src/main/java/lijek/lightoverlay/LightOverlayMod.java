package lijek.lightoverlay;

import net.minecraft.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.maths.Vec3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class LightOverlayMod {

    public static LightOverlayMod INSTANCE = new LightOverlayMod();
    public static boolean enabled = false;
    List<Marker> markers = new ArrayList<>();
    Minecraft mc;
    boolean toggleKeyPressed = false;
    int reloadTimer = 0;
    float pX, pY, pZ, mX, mY, mZ, x, y, z;

    public void tick(Minecraft mc){
        this.mc = mc;
        if(mc.level == null || mc.player == null)
            return;
        if(Keyboard.isKeyDown(Keyboard.KEY_F7) && !toggleKeyPressed){
            toggleKeyPressed = true;
            if(enabled){
                updateMarkers((int) mc.player.x, (int) mc.player.y, (int) mc.player.z);
            }
            enabled = !enabled;
        }else{
            toggleKeyPressed = false;
        }
    }

    public void render(float delta){

        pX = (float) mc.player.x;
        pY = (float) mc.player.y;
        pZ = (float) mc.player.z;

        mX = (float) mc.player.prevX;
        mY = (float) mc.player.prevY;
        mZ = (float) mc.player.prevZ;

        x = mX + ( pX - mX ) * delta;
        y = mY + ( pY - mY ) * delta;
        z = mZ + ( pZ - mZ ) * delta;

        if(reloadTimer > 0){
            reloadTimer--;
        }
        if(reloadTimer == 0) {
            updateMarkers((int)mc.player.x, (int)mc.player.y, (int)mc.player.z);
            reloadTimer = 20;
        }

        for (Marker m : markers) {
            Vec3f pos = fix(m.x, m.y, m.z);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3ub(m.r, m.g, m.b);

            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(pos.x+0.5f, pos.y,pos.z+0.5f);
            GL11.glVertex3d(pos.x-0.5f, pos.y,pos.z-0.5f);
            GL11.glVertex3d(pos.x+0.5f,pos.y,pos.z-0.5f);
            GL11.glVertex3d(pos.x-0.5f,pos.y,pos.z+0.5f);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    public Vec3f fix(float x, float y, float z){
        Vec3f position = Vec3f.from(x, y, z);
        position.x = position.x - this.x;
        position.y = position.y - this.y - mc.player.getHeightOffset() + 0.01d;
        position.z = position.z - this.z;

        return position;
    }

    private int getLightLevel(int x, int y, int z, int sunlight){
        return mc.level.getChunkFromCache(x >> 4, z >> 4).method_880(x & 0xf, y, z & 0xf, sunlight);
    }

    private void updateMarkers(int pX, int pY, int pZ){
        if(mc.level == null)
            return;

        markers = new ArrayList<>();

        for(int x=pX-16;x<pX+16;x++) for(int y=pY-16;y<pY+16;y++) for(int z=pZ-16;z<pZ+16;z++){
            int onWhat = mc.level.getTileId(x, y, z);
            int onWhat1 = mc.level.getTileId(x, y + 1, z);
            float yAddition = 0.0f;
            if(onWhat > 0){
                if(!BlockBase.BY_ID[onWhat].isFullOpaque())
                    continue;
            }else
                continue;
            if(onWhat1 != 0)
                if(BlockBase.BY_ID[onWhat1].isFullOpaque())
                    continue;

            if(!mc.level.isAir(x, y+1, z)){
                yAddition += 0.13f;
            }

            if(getLightLevel(x, y+1, z, 16) > 7)
                continue;
            markers.add(new Marker(x, y+2 + yAddition, z, (getLightLevel(x, y+1, z, 0) > 7)));
        }
    }

    private class Marker{
        public float x, y, z;
        public byte r, g, b;

        public Marker(float bx, float by, float bz, boolean sun) {
            x = 0.5f + bx;
            y = by + 0.13f;
            z = 0.5f + bz;
            r = (byte) 255;
            g = (byte) (sun ? 255 : 0);
            b = 0;
        }
    }

}
