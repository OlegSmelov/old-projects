class PrivateMessage < ActiveRecord::Base
  belongs_to :from, :class_name => "User"
  belongs_to :to, :class_name => "User"

  validates_presence_of :to, :message
end
