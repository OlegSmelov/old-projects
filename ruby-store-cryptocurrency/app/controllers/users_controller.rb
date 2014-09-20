class UsersController < ApplicationController
  include SessionsHelper

  before_filter :signed_in_user, only: [:edit, :update]
  before_filter :correct_user,   only: [:edit, :update]

  def index
    @users = User.all
  end

  def new
    @user = User.new
  end
  
  def create
    @user = User.new(permitted_params)
    if @user.save
      redirect_to root_url, flash: { success: "Signed up! You can login now." }
    else
      render :new
    end
  end

  def show
    @user = User.find_by_id(params[:id])
  end

  def edit
  end

  def update
    if @user && @user.update_attributes(permitted_params)
      flash[:success] = "Profile updated."
      sign_in @user
      redirect_to @user
    else
      render :edit
    end
  end

  private

    def permitted_params
      params.require(:user).permit(:name, :full_name, :email, :password, :password_confirmation)
    end

    def signed_in_user
      if not signed_in?
        redirect_to signin_url, notice: "Please sign in."
      end
    end

    def correct_user
      @user = User.find(params[:id])
      redirect_to(root_url) if not current_user?(@user)
    end
end
