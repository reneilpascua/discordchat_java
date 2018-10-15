package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {

    Guild guild;
    boolean echo = false;
    
    boolean settingRem = false;
    boolean setRemTime = false;
    User remUser;
    String rmd;
    
    boolean rpsGame = false;
    User rpsUser; // the user that is playing rock paper scissors
    private int myRPS;
    private String myRPS_;
    
    /**
     * log the bot in (given valid token)
     * @param args
     */
    public static void main(String[] args) {
        
        try {
            // jda object is the core of the library. must make jda object that contains out
            // bot token (setToken(String)). creating a jda object w valid token auto logins the bot
            // when the program is run
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken("NTAxMTYxMTcwNTkwMzY3NzQ1.DqVW-w.kAtdV78uyzSQ7WbRYY_UL05INLo")
                    .addEventListener(new DiscordBot()).buildBlocking();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * define responses to MessageReceivedEvent
     */
    public void onMessageReceived(MessageReceivedEvent event) {
        
        MessageChannel channel = event.getChannel(); // returns channel of the event
        Message message = event.getMessage();
        String msg = message.getContentDisplay(); // returns string representation of message
        User author = event.getAuthor(); // returns user that fired event
        Boolean isbot = author.isBot(); // true if user is a bot
        
        if (event.isFromType(ChannelType.TEXT)) { // guild(server)-specific
            guild = event.getGuild();
            TextChannel tchannel = event.getTextChannel();
            Member member = event.getMember(); // same as user but with guild-specific info
            
            List<Role> roles = new ArrayList<Role>();
            roles = member.getRoles();
            
            String name;
            if (message.isWebhookMessage()) {
                name = author.getName(); //If this is a Webhook message, then there is no Member associated
                                         // with the User, thus we default to the author for name.
            } else {
                name = member.getEffectiveName();       
              //This will either use the Member's nickname if they have one, 
              //otherwise it will default to their username. (User#getName())
            }
            
            // prints out chatlog in console
            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), tchannel.getName(), name, msg);
        }
        
        // from this point on, don't do anything if the last sender was a bot.
        if (isbot) {
            return;
        }
        
        // echo?
        if (echo) {
            channel.sendMessage(author.getName() + " said:\n" + msg).queue();
        }
        
        // greet
        if (msg.equalsIgnoreCase("hi")) {
            greeting(channel);
        }
        
        // lists commands
        if (msg.equalsIgnoreCase("!commands")) {
            commands(channel);
        }
        
        ////////////////////////////
        // REMINDER SETTING BLOCK //
        ////////////////////////////
        if (setRemTime && author == remUser) {
            try {
                int length = Integer.parseInt(msg);
                channel.sendMessage("ok, will remind you to:\n"+rmd+"\nin "+length
                        + " minute(s).").queue();
                setRemTime = false;
                channel.sendMessage(remUser.getAsMention() + "\n" + rmd + " NOW!!")
                .queueAfter(length, TimeUnit.MINUTES);
                remUser = null;
            } catch (Exception e) {
                channel.sendMessage("oops, something went wrong"
                        + "\nplease start over setting the reminder.").queue();
                setRemTime = false;
                remUser = null;
            }
        }
        
        if (settingRem && author == remUser) {
            rmd = msg;
            setRemTime = true;
            channel.sendMessage("in how many minutes?").queue();
            settingRem = false;
        }
        
        // starts reminder prompt
        if (!settingRem && !setRemTime && msg.equalsIgnoreCase("set reminder")) {
            settingRem = true;
            remUser = author;
            channel.sendMessage("what do you want me to remind you?\n"
                    + "ex.) take out the trash, do your homework").queue();
        }
        ///////// reminder part ends ////////////
        
        /////////////////////////////////////////
        /// ROCK PAPER SCISSORS BLOCK ///////////
        /////////////////////////////////////////
        if(author == rpsUser && rpsGame) {
            int userRPS=0;
            if (msg.equals("rock")) {
                userRPS = 0;
            } else if (msg.equals("paper")) {
                userRPS = 1;
            } else if (msg.equals("scissors")) {
                userRPS = 2;
            } else {
                channel.sendMessage("please enter one of rock, paper, or scissors").queue();
                return; // move on to the next response
            }
            if (calculateRPS(myRPS, userRPS)) {
                channel.sendMessage("I had "+myRPS_+ ". You win!").queue();
            } else {
                channel.sendMessage("I had "+myRPS_+ ". You lose!").queue();
            }
            rpsGame= false;
            rpsUser= null;
        }
        
        if(msg.equalsIgnoreCase("!rps")) {
            rpsUser = author;
            rpsGame = true;
            channel.sendMessage(rpsUser.getAsMention() + "let's play"
                    + " rock paper scissors!").queue();
            myRPS_ = RPS.values()[guessRPS()].name();
            
        }
        //////////// end of rock paper scissors block /////////
    }
    
    public void greeting(MessageChannel channel) {
        String greeting = "hello";
        channel.sendMessage(greeting).queue();
    }
    
    // helpful commands message for user
    public void commands(MessageChannel channel) {
        channel.sendMessage(
                "These are the available commands:\n"
                + "hi - prints greeting \"hey there\"")
        .queue();
    }
    
    private int guessRPS() {
        Random rng = new Random();
        myRPS = rng.nextInt(3);
        return myRPS;
    }
    
    public boolean calculateRPS(int myG, int userG) {
        return (userG == myG+1 || userG ==  myG-2);
    }
    
}

enum RPS {
    rock, paper, scissors
}
