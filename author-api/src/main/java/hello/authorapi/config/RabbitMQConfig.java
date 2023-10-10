package hello.authorapi.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khello.shared.constants.RabbitMQKeys;

@Configuration
public class RabbitMQConfig {
  public static final String QUEUE_BOOK_CREATED = "author-api.message.queue.book.created";
  public static final String QUEUE_BOOK_DELETED = "author-api.message.queue.book.deleted";
  public static final String QUEUE_BOOK_UPDATED = "author-api.message.queue.book.updated";

  // producer
  @Bean
  public FanoutExchange authorCreateExchange() {
    return new FanoutExchange(RabbitMQKeys.AUTHOR_CREATED_EXCHANGE);
  }

  @Bean
  public FanoutExchange authorUpdateExchange() {
    return new FanoutExchange(RabbitMQKeys.AUTHOR_UPDATED_EXCHANGE);
  }

  @Bean
  public FanoutExchange authorDeleteExchange() {
    return new FanoutExchange(RabbitMQKeys.AUTHOR_DELETED_EXCHANGE);
  }

  @Bean
  public MessageConverter messageConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @Bean
  public AmqpTemplate template(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    return template;
  }
}
