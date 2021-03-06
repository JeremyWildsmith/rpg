/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.rpg.entity;

import io.github.jevaengine.audio.IAudioClip;
import io.github.jevaengine.audio.IAudioClipFactory;
import io.github.jevaengine.audio.IAudioClipFactory.AudioClipConstructionException;
import io.github.jevaengine.config.*;
import io.github.jevaengine.config.IConfigurationFactory.ConfigurationConstructionException;
import io.github.jevaengine.rpg.entity.character.IRpgCharacter;
import io.github.jevaengine.rpg.entity.character.IRpgCharacterFactory;
import io.github.jevaengine.rpg.entity.character.IRpgCharacterFactory.CharacterCreationException;
import io.github.jevaengine.script.IScriptBuilder;
import io.github.jevaengine.script.IScriptBuilderFactory;
import io.github.jevaengine.script.IScriptBuilderFactory.ScriptBuilderConstructionException;
import io.github.jevaengine.script.NullScriptBuilder;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.entity.*;
import io.github.jevaengine.world.scene.model.IAnimationSceneModel;
import io.github.jevaengine.world.scene.model.IAnimationSceneModelFactory;
import io.github.jevaengine.world.scene.model.ISceneModel;
import io.github.jevaengine.world.scene.model.ISceneModelFactory;
import io.github.jevaengine.world.scene.model.ISceneModelFactory.SceneModelConstructionException;
import io.github.jevaengine.world.scene.model.particle.IParticleEmitter;
import io.github.jevaengine.world.scene.model.particle.IParticleEmitterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

public final class RpgEntityFactory implements IEntityFactory
{
	private final Logger m_logger = LoggerFactory.getLogger(RpgEntityFactory.class);
	private static final AtomicInteger m_unnamedEntityCount = new AtomicInteger();
	
	private final IParticleEmitterFactory m_particleEmitterFactory;
	private final IScriptBuilderFactory m_scriptBuilderFactory;
	private final IAudioClipFactory m_audioClipFactory;
	private final IConfigurationFactory m_configurationFactory;
	private final IRpgCharacterFactory m_characterFactory;
	private final IAnimationSceneModelFactory m_animationSceneModelFactory;
	private final ISceneModelFactory m_modelFactory;
	
	@Inject
	public RpgEntityFactory(IScriptBuilderFactory scriptBuilderFactory, IAudioClipFactory audioClipFactory, IConfigurationFactory configurationFactory,
			IRpgCharacterFactory characterFactory, IParticleEmitterFactory particleEmitterFactory, IAnimationSceneModelFactory animationSceneModelFactory,
							ISceneModelFactory modelFactory)
	{
		m_scriptBuilderFactory = scriptBuilderFactory;
		m_audioClipFactory = audioClipFactory;
		m_configurationFactory = configurationFactory;
		m_characterFactory = characterFactory;
		m_particleEmitterFactory = particleEmitterFactory;
		m_animationSceneModelFactory = animationSceneModelFactory;
		m_modelFactory = modelFactory;
	}
	
	@Override
	@Nullable
	public Class<? extends IEntity> lookup(String className)
	{
		for(RpgEntity e : RpgEntity.values())
		{
			if(e.getName().equals(className))
				return e.getEntityClass();
		}
		
		return null;
	}

	@Override
	@Nullable
	public <T extends IEntity> String lookup(Class<T> entityClass)
	{
		for(RpgEntity e : RpgEntity.values())
		{
			if(e.getEntityClass().equals(entityClass))
				return e.getName();
		}
		
		return null;
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, URI config) throws EntityConstructionException
	{
		IImmutableVariable configVar = new NullVariable();
		
		try
		{
			configVar = m_configurationFactory.create(config);
		} catch (ConfigurationConstructionException e)
		{
			m_logger.error("Unable to insantiate configuration for entity. Using null configuration instead.", e);
		}
		
		return create(entityClass, instanceName, configVar);
	}

	@Override
	public IEntity create(String entityName, @Nullable String instanceName, URI config) throws EntityConstructionException
	{
		IImmutableVariable configVar = new NullVariable();
		
		try
		{
			configVar = m_configurationFactory.create(config);
		} catch (ConfigurationConstructionException e)
		{
			m_logger.error("Unable to insantiate configuration for entity. Using null configuration instead.", e);
		}
		
		return create(entityName, instanceName, configVar);
	}
	
	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException
	{
		return create(entityClass, instanceName, URI.create(""), config);
	}

	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName) throws EntityConstructionException
	{
		return create(entityClass, instanceName, new NullVariable());
	}

	@Override
	public IEntity create(String entityName, @Nullable String instanceName, IImmutableVariable config) throws EntityConstructionException
	{
		Class<? extends IEntity> entityClass = lookup(entityName);
		
		if(entityClass == null)
			throw new EntityConstructionException(instanceName, new UnsupportedEntityTypeException(entityClass));

		return create(entityClass, instanceName, config);
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName) throws EntityConstructionException
	{
		return create(entityClass, instanceName, new NullVariable());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IEntity> T create(Class<T> entityClass, @Nullable String instanceName, URI config, IImmutableVariable auxConfig) throws EntityConstructionException
	{
		
		String configPath = config.getPath();
		
		IImmutableVariable varConfig = auxConfig;
		
		try
		{
			varConfig = new ImmutableVariableOverlay(varConfig, 
													configPath.isEmpty() || configPath.endsWith("/") ? new NullVariable() : m_configurationFactory.create(config));
		} catch (ConfigurationConstructionException e)
		{
			m_logger.error("Error occured constructing configuration for entity, ignoring external configuration and using just aux config.", e);
		}
		
		for(RpgEntity e : RpgEntity.values())
		{
			if(e.getEntityClass().equals(entityClass))
			{
				return (T)e.getBuilder().create(this,
																				instanceName == null ? this.getClass().getName() + m_unnamedEntityCount.getAndIncrement() : instanceName,
																				config, varConfig);
			}
		}
		
		throw new EntityConstructionException(entityClass.getName(), new UnsupportedEntityTypeException(entityClass));
	}

	@Override
	public IEntity create(String entityClass, @Nullable String instanceName, URI config, IImmutableVariable auxConfig) throws EntityConstructionException
	{
		Class<? extends IEntity> entityClazz = lookup(entityClass);
		
		if(entityClazz == null)
			throw new EntityConstructionException(instanceName, new UnsupportedEntityTypeException(entityClass));

		return create(entityClazz, instanceName, config, auxConfig);
	}
	
	public enum RpgEntity
	{
		ParticleDriver(ParticleDriver.class, "particleDriver", new EntityBuilder() {
			@Override
			public IEntity create(RpgEntityFactory entityFactory, String instanceName, URI context, IImmutableVariable auxConfig) throws EntityConstructionException
			{
				try
				{
					ParticleDriverDeclaration decl = auxConfig.getValue(ParticleDriverDeclaration.class);
					IParticleEmitter emitter = entityFactory.m_particleEmitterFactory.create(context.resolve(decl.particle));
					return new ParticleDriver(instanceName, emitter);
				} catch (SceneModelConstructionException | ValueSerializationException e)
				{
					throw new EntityConstructionException(e);
				}
			}
		}),
		
		Door(Door.class, "door", new EntityBuilder() {
			@Override
			public IEntity create(RpgEntityFactory entityFactory, String instanceName, URI context, IImmutableVariable auxConfig) throws EntityConstructionException
			{
				try
				{
				
					DoorDeclaration decl = auxConfig.getValue(DoorDeclaration.class);
					IAnimationSceneModel model = entityFactory.m_animationSceneModelFactory.create(context.resolve(new URI(decl.model)));
					return new Door(model, instanceName, decl.isOpen, decl.isLocked);
				} catch (SceneModelConstructionException | ValueSerializationException | URISyntaxException e)
				{
					throw new EntityConstructionException(e);
				}
			}
		}),
		
		AmbientAudioSource(AmbientAudioSource.class, "ambientAudioSource", new EntityBuilder() {
		@Override
			public IEntity create(RpgEntityFactory entityFactory, String instanceName, URI context, IImmutableVariable auxConfig) throws EntityConstructionException
			{
				try
				{
					AmbientAudioSourceDeclaration decl = auxConfig.getValue(AmbientAudioSourceDeclaration.class);
					
					IScriptBuilder behavior = entityFactory.m_scriptBuilderFactory.create(context.resolve(new URI(decl.behavior)));
					IAudioClip source = entityFactory.m_audioClipFactory.create(context.resolve(new URI(decl.audio)));
					source.setVolume(decl.volume);
					return new AmbientAudioSource(source, behavior, instanceName);
				} catch (ValueSerializationException | AudioClipConstructionException | URISyntaxException | ScriptBuilderConstructionException e)
				{
					throw new EntityConstructionException(e);
				}
			}
		}),
		
		AreaTrigger(AreaTrigger.class, "areaTrigger", new EntityBuilder() {
			@Override
			public IEntity create(RpgEntityFactory entityFactory, String name, URI context, IImmutableVariable config) throws EntityConstructionException
			{
				try
				{
					AreaTriggerDeclaration decl = config.getValue(AreaTriggerDeclaration.class);
					IScriptBuilder scriptBuilder = new NullScriptBuilder();
					
					try
					{
						if(decl.behavior != null)
						{
							URI behaviourUri = context.resolve(new URI(decl.behavior));
							scriptBuilder = entityFactory.m_scriptBuilderFactory.create(behaviourUri);
						}else
							scriptBuilder = entityFactory.m_scriptBuilderFactory.create();
					} catch (ScriptBuilderConstructionException | URISyntaxException e)
					{
						m_logger.error("Error constructing behavior for entity " + name +". Using null behavior instead.", e);
					}
					
					return new AreaTrigger(scriptBuilder, name, decl.searchZone);
				} catch (ValueSerializationException e)
				{
					throw new EntityConstructionException(RpgEntity.AreaTrigger.getName(), e);
				}
			}
		}),
		
		RpgCharacter(IRpgCharacter.class, "character", new EntityBuilder() {
			@Override
			public IEntity create(RpgEntityFactory entityFactory, String name, URI context, IImmutableVariable config) throws EntityConstructionException
			{
				try
				{
					if(config == null)
						return entityFactory.m_characterFactory.create(name, config);
					else
						return entityFactory.m_characterFactory.create(name, context, config);
				} catch (CharacterCreationException e)
				{
					throw new EntityConstructionException(RpgEntity.RpgCharacter.getName(), e);
				}
			}
		}),

		LogicController(LogicController.class, "logicController", new EntityBuilder() {
			@Override
			public IEntity create(RpgEntityFactory entityFactory, String instanceName, URI context, IImmutableVariable config) throws EntityConstructionException
			{
				try
				{
					LogicControllerDeclaration decl = config.getValue(LogicControllerDeclaration.class);
					IScriptBuilder scriptBuilder = new NullScriptBuilder();

					try
					{
						if(decl.behavior != null)
						{
							URI behaviourUri = context.resolve(new URI(decl.behavior));
							scriptBuilder = entityFactory.m_scriptBuilderFactory.create(behaviourUri);
						}else
							scriptBuilder = entityFactory.m_scriptBuilderFactory.create();
					} catch (ScriptBuilderConstructionException | URISyntaxException e)
					{
						m_logger.error("Error constructing behavior for entity " + instanceName +". Using null behavior instead.", e);
					}

					return new LogicController(new DefaultEntityTaskModelFactory(), scriptBuilder, instanceName);
				} catch (ValueSerializationException e)
				{
					throw new EntityConstructionException(RpgEntity.LogicController.getName(), e);
				}
			}
		}),

		SceneArtifact(SceneArtifact.class, "sceneArtifact", new EntityBuilder() {
			@Override
			public IEntity create(RpgEntityFactory entityFactory, String instanceName, URI context, IImmutableVariable config) throws EntityConstructionException
			{
				try
				{
					SceneArtifactDeclaration decl = config.getValue(SceneArtifactDeclaration.class);

					ISceneModel model = entityFactory.m_modelFactory.create(context.resolve(new URI(decl.model)));
					return new SceneArtifact(instanceName, model, true, !decl.blocking);
				} catch (SceneModelConstructionException | URISyntaxException | ValueSerializationException e)
				{
					throw new EntityConstructionException(RpgEntity.SceneArtifact.getName(), e);
				}
			}
		});

		private final Class<? extends IEntity> m_class;
		private final String m_name;
		private final EntityBuilder m_builder;

		RpgEntity(Class<? extends IEntity> clazz, String name, EntityBuilder builder)
		{
			m_class = clazz;
			m_name = name;
			m_builder = builder;
		}
		
		public String getName()
		{
			return m_name;
		}
		
		public Class<? extends IEntity> getEntityClass()
		{
			return m_class;
		}
		
		private EntityBuilder getBuilder()
		{
			return m_builder;
		}
		
		public static abstract class EntityBuilder
		{
			protected final Logger m_logger = LoggerFactory.getLogger(EntityBuilder.class);
			
			public abstract IEntity create(RpgEntityFactory entityFactory, String instanceName, URI context, IImmutableVariable auxConfig) throws EntityConstructionException;
		}
	}

	public static final class ParticleDriverDeclaration implements ISerializable
	{
		public String particle;

		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("particle").setValue(particle);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				particle = source.getChild("particle").getValue(String.class);
			} catch (NoSuchChildVariableException ex)
			{
				throw new ValueSerializationException(ex);
			}
		}
	}

	public static final class SceneArtifactDeclaration implements ISerializable
	{
		public String model;
		public boolean blocking;

		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("model").setValue(model);
			target.addChild("blocking").setValue(blocking);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				model = source.getChild("model").getValue(String.class);
				blocking = source.getChild("blocking").getValue(Boolean.class);
			} catch (NoSuchChildVariableException ex)
			{
				throw new ValueSerializationException(ex);
			}
		}
	}


	public static final class DoorDeclaration implements ISerializable
	{
		public String model;
		public boolean isOpen;
		public boolean isLocked;

		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("model").setValue(model);
			target.addChild("isOpen").setValue(isOpen);
			target.addChild("isLocked").setValue(isLocked);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				model = source.getChild("model").getValue(String.class);
				isOpen = source.getChild("isOpen").getValue(Boolean.class);

				if(source.childExists("isLocked"))
					isLocked = source.getChild("isLocked").getValue(Boolean.class);
				else
					isLocked = false;

			} catch (NoSuchChildVariableException ex)
			{
				throw new ValueSerializationException(ex);
			}
		}
	}
	
	public static final class AmbientAudioSourceDeclaration implements ISerializable
	{
		public String audio;
		public String behavior;
		public float volume;
		
		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("audio").setValue(audio);
			
			if(behavior != null)
				target.addChild("behavior").setValue(behavior);
			
			target.addChild("volume").setValue(volume);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				audio = source.getChild("audio").getValue(String.class);

				if(source.childExists("behavior"))
					behavior = source.getChild("behavior").getValue(String.class);
				
				volume = source.getChild("volume").getValue(Double.class).floatValue();
			} catch (NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}
	}
	
	public static final class LogicControllerDeclaration implements ISerializable
	{
		public String behavior;

		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			if(behavior != null)
				target.addChild("behavior").setValue(behavior);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
		
			try
			{
				behavior = source.getChild("behavior").getValue(String.class);
			} catch (NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}
	}
	
	public static class AreaTriggerDeclaration implements ISerializable
	{
		public String behavior;
		public String searchZone;
		
		public AreaTriggerDeclaration() { }
		
		@Override
		public void serialize(IVariable target) throws ValueSerializationException
		{
			target.addChild("searchZone").setValue(searchZone);
			if(behavior != null)
				target.addChild("behavior").setValue(behavior);
		}

		@Override
		public void deserialize(IImmutableVariable source) throws ValueSerializationException
		{
			try
			{
				this.searchZone = source.getChild("searchZone").getValue(String.class);
				
				if(source.childExists("behavior"))
					this.behavior = source.getChild("behavior").getValue(String.class);
			} catch(NoSuchChildVariableException e)
			{
				throw new ValueSerializationException(e);
			}
		}
	}
}
