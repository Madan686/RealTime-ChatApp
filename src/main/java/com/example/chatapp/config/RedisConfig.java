package com.example.chatapp.config;
import com.example.chatapp.service.RedisPublisherService;
import com.example.chatapp.service.RedisSubscriberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Bean
    public ChannelTopic chatTopic(){
        return new ChannelTopic(RedisPublisherService.CHAT_EVENTS_CHANNEL); 
    }
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisSubscriberService subscriberService){
        return new MessageListenerAdapter(subscriberService, "handleMessage");
    }
    @Bean
    public RedisMessageListenerContainer redisContainer(
        RedisConnectionFactory connectionFactory,
        MessageListenerAdapter listenerAdapter,
        ChannelTopic chatTopic
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, chatTopic());
        return container;
    }
}
