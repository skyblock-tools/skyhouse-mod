package tools.skyblock.skyhouse.mcmod.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.AhOverlayPositionEditor;

import java.util.Arrays;
import java.util.List;

public class AhOverlayPositionEditorCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "skyhouseeditahoverlay";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        SkyhouseMod.INSTANCE.getListener().openGui(new AhOverlayPositionEditor());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return Arrays.asList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
