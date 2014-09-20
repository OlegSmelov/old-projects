RSpec::Matchers.define :receive_message do |message|
  match do |user|
    user.inbox.include? message
  end

  failure_message_for_should do |user|
    "expected that #{user.name} would receive a message from #{message.from.name}"
  end

  failure_message_for_should_not do |user|
    "expected that #{user.name} would not receive a message from #{message.from.name}"
  end

  description do
    "receive a message from #{message.from.name}"
  end
end